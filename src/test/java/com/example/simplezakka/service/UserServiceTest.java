package com.example.simplezakka.service;

import com.example.simplezakka.dto.UserInfoResponse;
import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1);
        user.setUserName("テスト太郎");
        user.setUserEmail("test@example.com");
        user.setUserAddress("東京都渋谷区1-2-3");
    }

    @Nested
    @DisplayName("getUserInfo メソッドのテスト")
    class GetUserInfoTest {

        @Test
        @DisplayName("ユーザーが存在する場合、正しいDTOが返される")
        void getUserInfo_WhenUserExists_ShouldReturnUserInfoResponse() {
            // Arrange
            when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(user));

            // Act
            UserInfoResponse result = userService.getUserInfo("test@example.com");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(user.getUserName());
            assertThat(result.getEmail()).isEqualTo(user.getUserEmail());
            assertThat(result.getAddress()).isEqualTo(user.getUserAddress());

            verify(userRepository, times(1)).findByUserEmail("test@example.com");
        }

        @Test
        @DisplayName("ユーザーが存在しない場合、例外がスローされる")
        void getUserInfo_WhenUserNotFound_ShouldThrowException() {
            // Arrange
            when(userRepository.findByUserEmail("unknown@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserInfo("unknown@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("ユーザーが見つかりません");

            verify(userRepository, times(1)).findByUserEmail("unknown@example.com");
        }
    }

    @Nested
    @DisplayName("findByUserId メソッドのテスト")
    class FindByUserIdTest {

        @Test
        @DisplayName("指定したIDのユーザーが存在する場合、ユーザーが返される")
        void findByUserId_WhenExists_ShouldReturnUser() {
            // Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(user));

            // Act
            User result = userService.findByUserId(1);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserName()).isEqualTo("テスト太郎");

            verify(userRepository, times(1)).findById(1);
        }

        @Test
        @DisplayName("指定したIDのユーザーが存在しない場合、nullが返される")
        void findByUserId_WhenNotExists_ShouldReturnNull() {
            // Arrange
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // Act
            User result = userService.findByUserId(999);

            // Assert
            assertThat(result).isNull();

            verify(userRepository, times(1)).findById(999);
        }
    }
}