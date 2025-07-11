package com.example.simplezakka.service;

import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.UserSession;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import java.util.Optional;

import com.example.simplezakka.exception.AuthenticationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider; 
    }
    public LoginResponse findUserbyloginEmail(LoginRequest loginRequest) {
        String loginEmail = loginRequest.getLoginEmail();
        String loginPassword = loginRequest.getLoginPassword();
        Optional<User> userOpt = userRepository.findByUserEmail(loginEmail);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("ユーザーが見つかりません");
        }
        User user = userOpt.get();

        if (user.getUserPassword().equals(loginPassword)) {
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken);
        }
        else {
            throw new AuthenticationException("パスワードが間違っています");
        }
    }

}
