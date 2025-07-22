package com.example.simplezakka.service;

import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.entity.User;
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
        user.setUserPassword(password);  // 実際はハッシュ化されている想定

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

    // --- ログイン異常ケース（存在しないユーザー） ---
    @Test
    void findUserbyloginEmail_Unknown_ShouldThrowException() {
        String email = "no@example.com";
        String password = "123456";

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword(password);

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("ユーザーが見つかりません", thrown.getMessage());
    }

    // --- パスワード不一致 ---
    @Test
    void findUserbyloginEmail_Wrongpassword_ShouldThrowException() {
        String email = "test@example.com";
        String correctPassword = "correct-password"; // DBにあるパスワード
        String wrongPassword = "wrong-password";     // 入力された間違ったパスワード

        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(correctPassword);

        LoginRequest request = new LoginRequest();
        request.setUserEmail(email);
        request.setUserPassword(wrongPassword);

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("パスワードが間違っています", thrown.getMessage());
    }

    // --- Eメール空文字 ---  ← 追加したテスト
    @Test
    void findUserbyloginEmail_EmptyEmail_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUserEmail(""); // 空文字
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
        LoginRequest request = new LoginRequest();
        request.setUserEmail("test@example.com");
        request.setUserPassword("");

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.findUserbyloginEmail(request));

        assertEquals("パスワードが間違っています", thrown.getMessage());
    }

    @Test
    void findUserbyloginEmail_NullPassword_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUserEmail("test@example.com");
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

        when(userRepository.save(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(1);
            return u;
        });

        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        LoginResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(Integer.valueOf(1), response.getUserId());
    }

    // --- 登録異常ケース：既存メール ---
    @Test
    void registerUser_ExistingEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("test@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword("pass123");

        // 既存メールのテスト
        when(userRepository.findByUserEmail(request.getRegisterEmail())).thenReturn(Optional.of(new User()));

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    // --- 登録異常ケース：空・nullメール ---
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

    // --- 登録異常ケース：空・nullパスワード ---
    @Test
    void registerUser_NullPassword_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("new@example.com");
        request.setRegisterName("Taro");
        request.setRegisterAddress("Tokyo");
        request.setRegisterPassword(null);

        AuthenticationException thrown = assertThrows(AuthenticationException.class,
            () -> authService.registerUser(request));

        assertEquals("登録できませんでした", thrown.getMessage());
    }

    // --- 登録異常ケース：空・null名前 ---
    @Test
    void registerUser_EmptyName_ShouldThrowException() {
    }}