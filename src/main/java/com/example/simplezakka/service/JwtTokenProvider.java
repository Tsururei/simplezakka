package com.example.simplezakka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.example.simplezakka.entity.User;

<<<<<<< HEAD
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
=======
>>>>>>> bc800d9367670d2eaa7c57911be5f5030355f56e
import java.util.Date;

@Component
public class JwtTokenProvider {

<<<<<<< HEAD
=======
    @Value("${jwt.secret}")
    private String secretKey;

>>>>>>> bc800d9367670d2eaa7c57911be5f5030355f56e
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 3;

    public String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
<<<<<<< HEAD
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
=======
            .signWith(SignatureAlgorithm.HS256, secretKey)
>>>>>>> bc800d9367670d2eaa7c57911be5f5030355f56e
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
<<<<<<< HEAD
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
=======
            .signWith(SignatureAlgorithm.HS256, secretKey)
>>>>>>> bc800d9367670d2eaa7c57911be5f5030355f56e
            .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
