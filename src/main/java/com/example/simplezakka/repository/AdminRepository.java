package com.example.simplezakka.repository;

import com.example.simplezakka.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {

    // 管理者をメールアドレスで検索（ログイン時に使用）
    Optional<Admin> findByAdminEmail(String adminEmail);
}
