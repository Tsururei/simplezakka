package com.example.simplezakka.repository;

import com.example.simplezakka.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("ユーザーが存在する場合、メールアドレスで取得できる")
    void findByUserEmail_whenUserExists_returnsUser() {
        User user = User.builder()
                .userName("Test User")
                .userEmail("test@example.com")
                .userAddress("Tokyo")
                .userPassword("password")
                .userDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByUserEmail("test@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("ユーザーが存在しない場合、空のOptionalが返る")
    void findByUserEmail_whenUserDoesNotExist_returnsEmpty() {
        // Act
        Optional<User> result = userRepository.findByUserEmail("notfound@example.com");

        // Assert
        assertThat(result).isEmpty();
    }
}
