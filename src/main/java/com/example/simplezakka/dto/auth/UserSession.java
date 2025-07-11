package com.example.simplezakka.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSession {
    private String userName;
    private String userEmail;
    private String role;
}
