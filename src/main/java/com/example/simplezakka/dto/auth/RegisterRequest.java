package com.example.simplezakka.dto.auth;

import org.hibernate.annotations.CurrentTimestamp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "メールアドレス")
    @Email(message = "有効なメールアドレスを入力してください")
    private String registerEmail;

    @NotBlank(message = "パスワード")
    private String registerPassword;

    @NotBlank(message = "住所")
    private String registerAddress;
}