package com.example.simplezakka.controller;

import com.example.simplezakka.dto.MypageResponse;
import com.example.simplezakka.dto.UserInfoResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserService userService;

    private final String validToken = "valid.token";
    private final Integer userId = 1;

    private User createUser() {
        User user = new User();
        user.setUserId(userId);
        user.setUserName("テストユーザー");
        user.setUserEmail("test@example.com");
        user.setUserAddress("東京都渋谷区");
        user.setUserPassword("password123");
        return user;
    }

    // ========================
    // /api/user/me テスト群
    // ========================
    @Nested
    @DisplayName("GET /api/user/me")
    class GetCurrentUserTests {

        @Test
        @DisplayName("テスト1: 有効なトークンならユーザー情報を返す (200 OK)")
        void getCurrentUser_WhenValidToken_ShouldReturnUserInfo() throws Exception {
            User user = createUser();
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
            when(userService.findByUserId(userId)).thenReturn(user);

            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(user.getUserName()))
                    .andExpect(jsonPath("$.email").value(user.getUserEmail()))
                    .andExpect(jsonPath("$.address").value(user.getUserAddress()));

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verify(userService).findByUserId(userId);
        }

        @Test
        @DisplayName("テスト2: Authorizationヘッダーなしなら401")
        void getCurrentUser_WhenNoAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user/me")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(jwtTokenProvider, userService);
        }

        @Test
        @DisplayName("テスト3: Authorizationヘッダー形式不正なら401")
        void getCurrentUser_WhenInvalidAuthorizationHeaderFormat_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "InvalidFormat " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(jwtTokenProvider, userService);
        }

        @Test
        @DisplayName("テスト4: トークン解析に失敗したら401")
        void getCurrentUser_WhenInvalidToken_ShouldReturnUnauthorized() throws Exception {
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenThrow(new RuntimeException("Invalid token"));

            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("テスト5: ユーザーが見つからなければ404")
        void getCurrentUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
            when(userService.findByUserId(userId)).thenReturn(null);

            mockMvc.perform(get("/api/user/me")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verify(userService).findByUserId(userId);
        }
    }

    // ========================
    // /api/user/mypage テスト群
    // ========================
    @Nested
    @DisplayName("GET /api/user/mypage")
    class GetMypageUserTests {

        @Test
        @DisplayName("テスト1: 有効なトークンならマイページ情報を返す (200 OK)")
        void getMypageUser_WhenValidToken_ShouldReturnMypageInfo() throws Exception {
            User user = createUser();
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
            when(userService.findByUserId(userId)).thenReturn(user);

            mockMvc.perform(get("/api/user/mypage")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(user.getUserName()))
                    .andExpect(jsonPath("$.email").value(user.getUserEmail()))
                    .andExpect(jsonPath("$.address").value(user.getUserAddress()))
                    .andExpect(jsonPath("$.password").value(user.getUserPassword()));

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verify(userService).findByUserId(userId);
        }

        @Test
        @DisplayName("テスト2: Authorizationヘッダーなしなら401")
        void getMypageUser_WhenNoAuthorizationHeader_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user/mypage")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(jwtTokenProvider, userService);
        }

        @Test
        @DisplayName("テスト3: Authorizationヘッダー形式不正なら401")
        void getMypageUser_WhenInvalidAuthorizationHeaderFormat_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user/mypage")
                            .header("Authorization", "InvalidFormat " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(jwtTokenProvider, userService);
        }

        @Test
        @DisplayName("テスト4: トークン解析に失敗したら401")
        void getMypageUser_WhenInvalidToken_ShouldReturnUnauthorized() throws Exception {
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenThrow(new RuntimeException("Invalid token"));

            mockMvc.perform(get("/api/user/mypage")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("テスト5: ユーザーが見つからなければ404")
        void getMypageUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
            when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
            when(userService.findByUserId(userId)).thenReturn(null);

            mockMvc.perform(get("/api/user/mypage")
                            .header("Authorization", "Bearer " + validToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(jwtTokenProvider).getUserIdFromToken(validToken);
            verify(userService).findByUserId(userId);
        }
    }
}