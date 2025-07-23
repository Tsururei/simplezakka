package com.example.simplezakka.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.simplezakka.entity.Admin;
import com.example.simplezakka.exception.AuthenticationException;
import com.example.simplezakka.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

class AdminAuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminAuthService adminAuthService;

    private final String validEmail = "admin@example.com";
    private final String validPassword = "adminpass";
    private Admin sampleAdmin;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sampleAdmin = Admin.builder()
                .adminId("admin-id-123")
                .adminName("管理者 太郎")
                .adminEmail(validEmail)
                .adminPassword(validPassword)
                .adminDate(LocalDateTime.now())
                .build();
    }

    @Test
    void authenticate_Valid_ReturnsAdminSession() {
        when(adminRepository.findByAdminEmail(validEmail)).thenReturn(Optional.of(sampleAdmin));

        var session = adminAuthService.authenticate(validEmail, validPassword);

        assertNotNull(session);
        assertEquals(sampleAdmin.getAdminId(), session.getAdminId());
        assertEquals(sampleAdmin.getAdminName(), session.getAdminName());
        assertEquals(sampleAdmin.getAdminEmail(), session.getAdminEmail());
        assertEquals("ADMIN", session.getRole());
    }

    @Test
    void authenticate_AdminNotFound_ThrowsException() {
        when(adminRepository.findByAdminEmail("no@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminAuthService.authenticate("no@example.com", validPassword));

        assertEquals("管理者が見つかりません", ex.getMessage());
    }

    @Test
    void authenticate_WrongPassword_ThrowsException() {
        when(adminRepository.findByAdminEmail(validEmail)).thenReturn(Optional.of(sampleAdmin));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminAuthService.authenticate(validEmail, "wrongpass"));

        assertEquals("パスワードが正しくありません", ex.getMessage());
    }

    

    @Test
    void authenticate_EmptyEmail_ThrowsException() {
        
        when(adminRepository.findByAdminEmail("")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminAuthService.authenticate("", validPassword));

        assertEquals("管理者が見つかりません", ex.getMessage());
    }

    @Test
    void authenticate_EmptyPassword_ThrowsException() {
        when(adminRepository.findByAdminEmail(validEmail)).thenReturn(Optional.of(sampleAdmin));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminAuthService.authenticate(validEmail, ""));

        assertEquals("パスワードが正しくありません", ex.getMessage());
    }

    

    @Test
    void authenticate_NullPassword_ShouldThrowException() {
        when(adminRepository.findByAdminEmail(validEmail)).thenReturn(Optional.of(sampleAdmin));
    
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                adminAuthService.authenticate(validEmail, null));
        assertEquals("パスワードが正しくありません", ex.getMessage());
    }
}