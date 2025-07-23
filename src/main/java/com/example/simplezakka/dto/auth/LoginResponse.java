package com.example.simplezakka.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Integer userId;

    public LoginResponse(String accessToken, String refreshToken, Integer userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}

