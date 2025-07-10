package com.example.simplezakka.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSession {
    private String admin_id;
    private String admin_name;
    private String admin_email;
    private String role;  // 例: "ADMIN"
}
