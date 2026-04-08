package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.dto.ForgotPasswordRequestDto;
import com.digitalmoneyhouse.authservice.dto.ResetPasswordRequestDto;
import com.digitalmoneyhouse.authservice.entity.PasswordResetToken;
import com.digitalmoneyhouse.authservice.entity.AuthUser;
import com.digitalmoneyhouse.authservice.repository.PasswordResetTokenRepository;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordRecoveryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                passwordRecoveryService,
                "resetPasswordUrl",
                "http://localhost:3000/reset-password"
        );
    }

    // Test: forgotPassword sends email
    @Test
    void shouldGenerateTokenAndSendResetPasswordEmail() {

        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto();
        request.setEmail("test@test.com");

        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        passwordRecoveryService.forgotPassword(request);

        verify(tokenRepository).deleteByUserId(1L);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendResetPasswordEmail(eq("test@test.com"), contains("token"));
    }

    // Test: forgotPassword when user does NOT exist
    @Test
    void shouldDoNothingWhenUserDoesNotExist() {

        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto();
        request.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        passwordRecoveryService.forgotPassword(request);

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendResetPasswordEmail(any(), any());
    }

    // Test: resetPassword successful
    @Test
    void shouldResetPasswordSuccessfully() {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("token123");
        request.setNewPassword("newPass");
        request.setRepeatPassword("newPass");

        AuthUser user = new AuthUser();
        user.setId(1L);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token123");
        token.setUser(user);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("token123"))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedPass");

        passwordRecoveryService.resetPassword(request);

        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }

    // Test: passwords do not match
    @Test
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setNewPassword("pass1");
        request.setRepeatPassword("pass2");

        assertThrows(
                ValidationException.class,
                () -> passwordRecoveryService.resetPassword(request)
        );
    }

    // Test: invalid token
    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("invalid");
        request.setNewPassword("pass");
        request.setRepeatPassword("pass");

        when(tokenRepository.findByToken("invalid"))
                .thenReturn(Optional.empty());

        assertThrows(
                ValidationException.class,
                () -> passwordRecoveryService.resetPassword(request)
        );
    }

    // Test: expired token
    @Test
    void shouldThrowExceptionWhenTokenExpired() {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("token123");
        request.setNewPassword("pass");
        request.setRepeatPassword("pass");

        AuthUser user = new AuthUser();

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token123");
        token.setUser(user);
        token.setExpirationDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("token123"))
                .thenReturn(Optional.of(token));

        assertThrows(
                ValidationException.class,
                () -> passwordRecoveryService.resetPassword(request)
        );
    }
}
