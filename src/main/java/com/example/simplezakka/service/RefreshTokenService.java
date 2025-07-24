package com.example.simplezakka.service;

import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public RefreshTokenService(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public String reissueAccessToken(String refreshToken) {
        Integer userId = tokenProvider.getUserIdFromToken(refreshToken);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return tokenProvider.generateAccessToken(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
