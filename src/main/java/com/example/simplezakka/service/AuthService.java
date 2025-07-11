package com.example.simplezakka.service;

import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.UserSession;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.simplezakka.exception.AuthenticationException;
import jakarta.servlet.http.HttpSession;

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
            Integer userId = user.getUserId();

            return new LoginResponse(accessToken, refreshToken, userId);
        }
        else {
            throw new AuthenticationException("パスワードが間違っています");
        }
    }

    public LoginResponse registerUser(RegisterRequest request) {
        String registerEmail = request.getRegisterEmail();
        Optional<User> userOpt = userRepository.findByUserEmail(registerEmail);
        if (userOpt.isEmpty()) {

            User user = new User();
            user.setUserEmail(registerEmail);
            user.setUserAddress(request.getRegisterAddress());
            user.setUserPassword(request.getRegisterPassword());
            user.setUserName(request.getRegisterName());
            user.setUserDate(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
            String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);
            Integer userId = savedUser.getUserId();

            return new LoginResponse(accessToken, refreshToken, userId);    
        }
        else {
            throw new AuthenticationException("登録できませんでした");
        }
        
    }    

}
