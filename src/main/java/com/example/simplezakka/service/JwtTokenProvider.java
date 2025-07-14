package com.example.simplezakka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.example.simplezakka.entity.User;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 3;

    public String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}