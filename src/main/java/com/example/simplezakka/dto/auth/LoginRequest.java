package com.example.simplezakka.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "メールアドレス")
    @Email(message = "有効なメールアドレスを入力してください")
    private String LoginEmail;

    @NotBlank(message = "パスワード")
    private String LoginPassword;
}
