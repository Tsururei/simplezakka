package com.example.simplezakka.service;

import com.example.simplezakka.dto.UserInfoResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        return UserInfoResponse.builder()
                .name(user.getUserName())
                .email(user.getUserEmail())
                .address(user.getUserAddress())
                .build();
    }

    public User findByUserId(Integer userId) {
    return userRepository.findById(userId).orElse(null);
}

}
