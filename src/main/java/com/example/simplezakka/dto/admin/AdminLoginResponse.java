package com.example.simplezakka.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginResponse {
    private String admin_name;
    private String role;
}
