package com.example.simplezakka.controller;

import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponce;
import com.example.simplezakka.dto.auth.UserSession;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponce tokens = authService.authenticate(request.getLoginEmail(),request.getLoginPassword());
            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認証失敗: " + e.getMessage());
        }
    }
}
