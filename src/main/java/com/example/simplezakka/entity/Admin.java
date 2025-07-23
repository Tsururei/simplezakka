package com.example.simplezakka.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admins")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @Column(name = "admin_id", nullable = false, unique = true, updatable = false)
    private String adminId;

    @NotBlank(message = "名前は必須です")
    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    @Column(name = "admin_email", nullable = false, unique = true)
    private String adminEmail;

    @NotBlank(message = "パスワードは必須です")
    @Column(name = "admin_password", nullable = false)
    private String adminPassword;

    @Column(name = "admin_date", nullable = false)
    private LocalDateTime adminDate;

    // ハッシュ化なしの簡易パスワードチェック
    public boolean isValidPassword(String rawPassword) {
        return this.adminPassword.equals(rawPassword);
    }
}
