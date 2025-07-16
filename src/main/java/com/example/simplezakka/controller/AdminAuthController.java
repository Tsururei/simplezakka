package com.example.simplezakka.controller;

import com.example.simplezakka.dto.admin.AdminSession;
import com.example.simplezakka.service.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController  
@CrossOrigin(origins = "http://127.0.0.1:5500" ,allowCredentials = "true")
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    // HTMLフォームからの管理者ログイン
    @PostMapping("/login")
    public String loginForm(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        try {
            AdminSession adminSession = adminAuthService.authenticate(email, password);
            session.setAttribute("ADMIN_SESSION", adminSession);
            return "redirect:/admin-top.html";  // 管理画面トップへリダイレクト
        } catch (Exception e) {
            // 認証失敗時（ログイン画面に戻す）
            return "redirect:/admin-login.html";
        }
    }

    // ログアウト処理（フォームなどからPOSTされた場合）
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
