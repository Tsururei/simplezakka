package com.example.simplezakka.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSession {
    private String adminId;
    private String adminName;
    private String adminEmail;
    private String role;  // 例: "ADMIN"
}
