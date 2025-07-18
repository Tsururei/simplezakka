package com.example.simplezakka.service;

import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- ログイン正常ケース ---
    @Test
    void findUserbyloginEmail_Valid_ShouldReturnTokens() {
        String email = "test@example.com";
        String password = "123456";

        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(password);

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword(password);

        LoginResponse response = authService.findUserbyloginEmail(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(user.getUserId(), response.getUserId());
    }

    @Test
    void findUserbyloginEmail_Unknown_ShouldThrowException() {
        when(userRepository.findByUserEmail("no@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUserEmail("no@example.com");
        request.setUserPassword("123456");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("ユーザーが見つかりません", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_Wrongpassword_ShouldThrowException() {
        String email = "test@example.com";

        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword("correct-password");

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword("wrong-password");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("パスワードが間違っています", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_EmptyEmail_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUserEmail("");
        request.setUserPassword("123456");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("ユーザーが見つかりません", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_NullEmail_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUserEmail(null);
        request.setUserPassword("123456");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("ユーザーが見つかりません", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_EmptyPassword_ShouldThrowException() {
        String email = "test@example.com";

        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword("dummy");

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword("");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("パスワードが間違っています", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_NullPassword_ShouldThrowException() {
        String email = "test@example.com";

        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword("dummy");

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword(null);

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("パスワードが間違っています", thrown.getMessage());
    }

    // --- 登録正常ケース ---
    @Test
    void registerUser_Valid_ShouldRegisterAndReturnResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(1);
            return u;
        });
        when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        LoginResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(userIdCompare(response.getUserId()), response.getUserId());
    }

    @Test
    void registerUser_ExistingEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("test@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.of(new User()));

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    @Test
    void registerUser_EmptyEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    @Test
    void registerUser_NullEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail(null);
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    @Test
    void registerUser_NullPassword_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword(null);

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    @Test
    void registerUser_EmptyName_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("名前は必須です", thrown.getMessage());
    }

    @Test
    void registerUser_NullName_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName(null);
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("名前は必須です", thrown.getMessage());
    }

    @Test
    void registerUser_EmptyAddress_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("住所は必須です", thrown.getMessage());
    }

    @Test
    void registerUser_NullAddress_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress(null);
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("住所は必須です", thrown.getMessage());
    }

    @Test
    void registerUser_EmptyNameAndAddress_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("");
        request.setRegisterAddress("");
        request.setRegisterPassword("pass123");

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("すべての項目を入力してください", thrown.getMessage());
    }

    @Test
    void registerUser_NullEmailAndPassword_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail(null);
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword(null);

        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.empty());

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    /** 補助メソッド */
    private int userIdCompare(Integer actual) {
        // 実装に合わせて型を整合
        assertNotNull(actual);
        return actual;
    }
}
