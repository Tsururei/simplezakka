package com.example.simplezakka.controller;

import com.example.simplezakka.dto.admin.AdminSession;
import com.example.simplezakka.service.AdminAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController  
@CrossOrigin(origins = "http://127.0.0.1:5500" ,allowCredentials = "true")
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        try {
            AdminSession adminSession = adminAuthService.authenticate(email, password);
            session.setAttribute("ADMIN_SESSION", adminSession);
            
            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログイン失敗");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie cookie = new Cookie("JSESSIONID", "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
