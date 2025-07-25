package com.example.simplezakka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponse;
import com.example.simplezakka.dto.auth.RegisterRequest;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse findUserbyloginEmail(LoginRequest loginRequest) {
        String loginEmail = loginRequest.getUserEmail();
        String loginPassword = loginRequest.getUserPassword();

        if (loginEmail == null || loginEmail.isEmpty()) {
            throw new AuthenticationException("ユーザーが見つかりません");
        }

        if (loginPassword == null || loginPassword.isEmpty()) {
            throw new AuthenticationException("パスワードが間違っています");
        }

        Optional<User> userOpt = userRepository.findByUserEmail(loginEmail);
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("ユーザーが見つかりません");
        }

        User user = userOpt.get();

        if (!user.getUserPassword().equals(loginPassword)) {
            throw new AuthenticationException("パスワードが間違っています");
        }

        return new LoginResponse(
            jwtTokenProvider.generateAccessToken(user),
            jwtTokenProvider.generateRefreshToken(user),
            user.getUserId()
        );
    }

    public LoginResponse registerUser(RegisterRequest request) {
        String email = request.getRegisterEmail();
        String name = request.getRegisterName();
        String address = request.getRegisterAddress();
        String password = request.getRegisterPassword();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("登録できませんでした");
        }

        if (name == null || name.isEmpty()) {
            if (address == null || address.isEmpty()) {
                throw new AuthenticationException("すべての項目を入力してください");
            }
            throw new AuthenticationException("名前は必須です");
        }

        if (address == null || address.isEmpty()) {
            throw new AuthenticationException("住所は必須です");
        }

        if (userRepository.findByUserEmail(email).isPresent()) {
            throw new AuthenticationException("メールアドレスが重複しています");
        }

        User user = new User();
        user.setUserEmail(email);
        user.setUserName(name);
        user.setUserAddress(address);
        user.setUserPassword(password);
        user.setUserDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return new LoginResponse(
            jwtTokenProvider.generateAccessToken(savedUser),
            jwtTokenProvider.generateRefreshToken(savedUser),
            savedUser.getUserId()
        );
    }
}
