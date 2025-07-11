package com.example.simplezakka.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @Column(name = "admin_id", nullable = false, unique = true, updatable = false)
    private String adminId;

    @Column(name = "admin_name", nullable = false)
    private String adminName;

    @Column(name = "admin_email", nullable = false, unique = true)
    private String adminEmail;

    @Column(name = "admin_password", nullable = false)
    private String adminPassword;

    @Column(name = "admin_date", nullable = false)
    private LocalDateTime adminDate;

    // ハッシュ化なしの簡易パスワードチェック
    public boolean isValidPassword(String rawPassword) {
        return this.adminPassword.equals(rawPassword);
    }
}
