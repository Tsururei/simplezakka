package com.example.simplezakka.controller;

import com.example.simplezakka.dto.MypageResponse;
import com.example.simplezakka.dto.UserInfoResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.service.JwtTokenProvider;
import com.example.simplezakka.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public UserController(JwtTokenProvider jwtTokenProvider, UserService userService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }


    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@RequestHeader("Authorization")String authorizationHeader) {
       if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authorizationHeader.substring(7);
        Integer userId;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        UserInfoResponse response = UserInfoResponse.builder()
                .name(user.getUserName())
                .email(user.getUserEmail())
                .address(user.getUserAddress())
                .build();

        return ResponseEntity.ok(response);
    }

        @GetMapping("/mypage")
    public ResponseEntity<MypageResponse> getMypageUser(@RequestHeader("Authorization")String authorizationHeader) {
       if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authorizationHeader.substring(7);
        Integer userId;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        MypageResponse response = MypageResponse.builder()
                .name(user.getUserName())
                .email(user.getUserEmail())
                .address(user.getUserAddress())
                .password(user.getUserPassword())
                .build();

        return ResponseEntity.ok(response);
    }
}