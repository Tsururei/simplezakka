package com.example.simplezakka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginAndRegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisterAndLogin() throws Exception {
        // 会員登録リクエスト（新規メールアドレスにしてください）
        String registerJson = """
        {
            "registerEmail": "testuser@example.com",
            "registerPassword": "testpassword",
            "registerAddress": "東京都港区",
            "registerName": "山田おいじょい"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.userId").exists());

        // ログインリクエスト
        String loginJson = """
        {
            "loginEmail": "testuser@example.com",
            "loginPassword": "testpassword"
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.userId").exists());
    }
}
