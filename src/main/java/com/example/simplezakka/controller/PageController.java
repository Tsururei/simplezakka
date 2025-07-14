package com.example.simplezakka.controller;

import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.service.AuthService;
import com.example.simplezakka.exception.AuthenticationException;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class PageController {

    @Autowired
    private AuthService authService;

    // 登録ページを表示
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // templates/register.html を表示
    }

    // 登録フォームのPOST処理
    @PostMapping("/register")
    public String registerUser(
        @RequestParam("user_name") String userName,
        @RequestParam("user_address") String userAddress,
        @RequestParam("user_email") String userEmail,
        @RequestParam("user_password") String userPassword,
        HttpSession session) {

        RegisterRequest request = new RegisterRequest();
        request.setRegisterName(userName);
        request.setRegisterAddress(userAddress);
        request.setRegisterEmail(userEmail);
        request.setRegisterPassword(userPassword);

        try {
            LoginResponse tokens = authService.registerUser(request);
            session.setAttribute("accessToken", tokens.getAccessToken());
            session.setAttribute("refreshToken", tokens.getRefreshToken());
            return "redirect:/auth/home"; // 登録成功 → ホーム画面へ
        } catch (AuthenticationException e) {
            session.setAttribute("registerError", "登録失敗: " + e.getMessage());
            return "redirect:/auth/register"; // 登録失敗 → 再表示
        }
    }

    // ホームページ表示
    @GetMapping("/home")
    public String showHomePage() {
        return "redirect:/home.html"; // templates/home.html を表示
    }
}
