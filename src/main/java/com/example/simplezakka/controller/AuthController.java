package com.example.simplezakka.controller;

import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.service.AuthService;
import com.example.simplezakka.service.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            LoginResponse tokens = authService.findUserbyloginEmail(request);

            session.setAttribute("accessToken",tokens.getAccessToken());
            session.setAttribute("refreshToken", tokens.getRefreshToken());
            return "redirect:/home.html";
            
        } catch (AuthenticationException e) {
            session.setAttribute("loginError", "ログイン失敗 " + e.getMessage());
            return "redirect:/index.html";
        }
    }

    @PostMapping("/register")
    public String register(    
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
            session.setAttribute("accessToken",tokens.getAccessToken());
            session.setAttribute("refreshToken", tokens.getRefreshToken());
            return "redirect:/home.html";
        } catch (AuthenticationException e) {
            session.setAttribute("registerError","登録失敗" + e.getMessage());
            return "redirect:/index.html";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("UserSession");
        return "redirect:/index.html";
    }
}
