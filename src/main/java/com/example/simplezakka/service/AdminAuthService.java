package com.example.simplezakka.service;

import com.example.simplezakka.dto.admin.AdminSession;
import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;

    @Transactional
    public AdminSession authenticate(String email, String password) {
        Admin admin = adminRepository.findByAdminEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("管理者が見つかりません"));

        if (!admin.isValidPassword(password)) {
            throw new IllegalArgumentException("パスワードが正しくありません");
        }

        return new AdminSession(
                admin.getAdminId(),
                admin.getAdminName(),
                admin.getAdminEmail(),
                "ADMIN"
        );
    }
}