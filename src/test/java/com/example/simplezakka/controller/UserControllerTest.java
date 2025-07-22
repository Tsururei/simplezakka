package com.example.simplezakka.controller;

import com.example.simplezakka.dto.MypageResponse;
import com.example.simplezakka.dto.UserInfoResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    private final String validToken = "valid.jwt.token";
    private final String invalidToken = "invalid.jwt.token";
    private final String nonExistingToken = "valid.jwt.token.nonexisting";
    private final String authHeader = "Bearer " + validToken;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setUserName("Test User");
        mockUser.setUserEmail("test@example.com");
        mockUser.setUserAddress("Tokyo");
        mockUser.setUserPassword("securepassword");
    }

    // ✅ 1. 認証成功時のユーザー情報取得（正常系）

    @Test
    void getLoginUser_withValidToken_returnsUserInfo() {
        when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(1);
        when(userService.findByUserId(1)).thenReturn(mockUser);

        ResponseEntity<UserInfoResponse> response = userController.getCurrentUser(authHeader);

        assertEquals(200, response.getStatusCodeValue());
        UserInfoResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Test User", body.getName());
        assertEquals("test@example.com", body.getEmail());
        assertEquals("Tokyo", body.getAddress());
    }

    @Test
    void getLoginUserMypage_withValidToken_returnsMypageInfo() {
        when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(1);
        when(userService.findByUserId(1)).thenReturn(mockUser);

        ResponseEntity<MypageResponse> response = userController.getMypageUser(authHeader);

        assertEquals(200, response.getStatusCodeValue());
        MypageResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Test User", body.getName());
        assertEquals("test@example.com", body.getEmail());
        assertEquals("Tokyo", body.getAddress());
        assertEquals("securepassword", body.getPassword());
    }

    // ✅ 2. Authorizationヘッダーなし（異常系）

    @Test
    void getGuestUser_withoutAuthorizationHeader_returns401() {
        ResponseEntity<UserInfoResponse> response = userController.getCurrentUser(null);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void getGuestUserMypage_withoutAuthorizationHeader_returns401() {
        ResponseEntity<MypageResponse> response = userController.getMypageUser(null);
        assertEquals(401, response.getStatusCodeValue());
    }

    // ✅ 3. Authorization形式不正（異常系）

    @Test
    void getGuestUser_withInvalidAuthorizationFormat_returns401() {
        ResponseEntity<UserInfoResponse> response = userController.getCurrentUser("InvalidTokenFormat");
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void getGuestUserMypage_withInvalidAuthorizationFormat_returns401() {
        ResponseEntity<MypageResponse> response = userController.getMypageUser("InvalidTokenFormat");
        assertEquals(401, response.getStatusCodeValue());
    }

    // ✅ 4. JWTトークン解析失敗（異常系）

    @Test
    void getGuestUser_withInvalidToken_returns401() {
        when(jwtTokenProvider.getUserIdFromToken(invalidToken)).thenThrow(new RuntimeException());
        ResponseEntity<UserInfoResponse> response = userController.getCurrentUser("Bearer " + invalidToken);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void getGuestUserMypage_withInvalidToken_returns401() {
        when(jwtTokenProvider.getUserIdFromToken(invalidToken)).thenThrow(new RuntimeException());
        ResponseEntity<MypageResponse> response = userController.getMypageUser("Bearer " + invalidToken);
        assertEquals(401, response.getStatusCodeValue());
    }

    // ✅ 5. ユーザー情報未登録（異常系）

    @Test
    void getLoginUser_withValidTokenButUserNotFound_returns404() {
        when(jwtTokenProvider.getUserIdFromToken(nonExistingToken)).thenReturn(999);
        when(userService.findByUserId(999)).thenReturn(null);

        ResponseEntity<UserInfoResponse> response = userController.getCurrentUser("Bearer " + nonExistingToken);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getLoginUserMypage_withValidTokenButUserNotFound_returns404() {
        when(jwtTokenProvider.getUserIdFromToken(nonExistingToken)).thenReturn(999);
        when(userService.findByUserId(999)).thenReturn(null);

        ResponseEntity<MypageResponse> response = userController.getMypageUser("Bearer " + nonExistingToken);
        assertEquals(404, response.getStatusCodeValue());
    }
}
