package com.example.simplezakka.service;

import org.hibernate.annotations.processing.Find;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.simplezakka.dto.auth.LoginRequest;
import com.example.simplezakka.dto.auth.LoginResponce;
import com.example.simplezakka.dto.auth.UserSession;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    @Autowired
    public AuthService(UserRepository userRepository, Object passwordEncoder){
        this.userRepository = userRepository; 
    }
    public Optional<User> findUserbyloginEmail(String loginEmail) {
        return userRepository.findByUserEmail(loginEmail);
    }
}
