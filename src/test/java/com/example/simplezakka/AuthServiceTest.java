package com.example.simplezakka;

import com.example.simplezakka.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceTest {


    private static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);


    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserbyloginEmail_Success() {
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setUserId(1);
        user.setUserEmail(email);
        user.setUserPassword(password);

        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(user)).thenReturn("refresh-token");

        LoginRequest request = new LoginRequest();
        request.setLoginEmail(email);
        request.setLoginPassword(password);

        LoginResponse response = authService.findUserbyloginEmail(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(1, response.getUserId());

        logger.info("登録成功: ユーザーID={}, AccessToken={}, RefreshToken={}",
        response.getUserId(),
        response.getAccessToken(),
        response.getRefreshToken());
    }

    @Test
    void testFindUserbyloginEmail_UserNotFound() {
        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setLoginEmail("notfound@example.com");
        request.setLoginPassword("pass");

        AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> {
            authService.findUserbyloginEmail(request);
        });

        assertTrue(thrown.getMessage().contains("ユーザーが見つかりません"));
    }

    @Test
    void testFindUserbyloginEmail_WrongPassword() {
        User user = new User();
        user.setUserEmail("test@example.com");
        user.setUserPassword("correct-password");

        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setLoginEmail("test@example.com");
        request.setLoginPassword("wrong-password");

        AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> {
            authService.findUserbyloginEmail(request);
        });

        assertTrue(thrown.getMessage().contains("パスワードが間違っています"));
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("newuser@example.com");
        request.setRegisterPassword("pass123");
        request.setRegisterName("New User");
        request.setRegisterAddress("Tokyo");

        when(userRepository.findByUserEmail("newuser@example.com")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setUserId(10);
        savedUser.setUserEmail(request.getRegisterEmail());
        savedUser.setUserPassword(request.getRegisterPassword());
        savedUser.setUserName(request.getRegisterName());
        savedUser.setUserAddress(request.getRegisterAddress());
        savedUser.setUserDate(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(savedUser)).thenReturn("refresh-token");

        LoginResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(10, response.getUserId());

        logger.info("登録成功: ユーザーID={}, AccessToken={}, RefreshToken={}",
        response.getUserId(),
        response.getAccessToken(),
        response.getRefreshToken());
    }

    @Test
    void testRegisterUser_AlreadyExists() {
        User existingUser = new User();
        existingUser.setUserEmail("exist@example.com");

        when(userRepository.findByUserEmail("exist@example.com")).thenReturn(Optional.of(existingUser));

        RegisterRequest request = new RegisterRequest();
        request.setRegisterEmail("exist@example.com");
        request.setRegisterPassword("pass");
        request.setRegisterName("Exist User");
        request.setRegisterAddress("Osaka");

        AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> {
            authService.registerUser(request);
        });

        assertTrue(thrown.getMessage().contains("登録できませんでした"));
    }
}
