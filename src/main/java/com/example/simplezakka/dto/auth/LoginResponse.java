package com.example.simplezakka.dto.auth;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // getter は最低限必要（Lombok @Getter でもOK）
}

