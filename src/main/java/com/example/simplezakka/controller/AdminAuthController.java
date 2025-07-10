package com.example.simplezakka.controller;

import com.example.simplezakka.dto.admin.AdminLoginRequest;
import com.example.simplezakka.dto.admin.AdminLoginResponse;
import com.example.simplezakka.dto.admin.AdminSession;
import com.example.simplezakka.service.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    // 管理者ログイン
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpSession session
    ) {
        AdminSession adminSession = adminAuthService.authenticate(
                request.getAdmin_email(),
                request.getAdmin_password()
        );

        // セッションに管理者情報を保存
        session.setAttribute("ADMIN_SESSION", adminSession);

        // レスポンスを生成して返す
        AdminLoginResponse response = new AdminLoginResponse(
                adminSession.getAdmin_name(),
                adminSession.getRole()
        );
        return ResponseEntity.ok(response);
    }

    // 管理者ログアウト
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.removeAttribute("ADMIN_SESSION");
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
