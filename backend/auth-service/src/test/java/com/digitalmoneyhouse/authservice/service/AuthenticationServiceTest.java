package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.dto.AuthenticationRequestDto;
import com.digitalmoneyhouse.authservice.dto.AuthenticationResponseDto;
import com.digitalmoneyhouse.authservice.dto.VerifyEmailRequestDto;
import com.digitalmoneyhouse.authservice.entity.AuthUser;
import com.digitalmoneyhouse.authservice.exception.UserNotFoundException;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    // Login successful
    @Test
    void shouldLoginSuccessfully() {

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setPassword("encoded-password");
        user.setEmailVerified(true);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        AuthenticationResponseDto response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());

        verify(jwtService).generateToken(user);
    }

    // User does not exist
    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authenticationService.login(request));
    }

    // Incorrect password
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setPassword("encoded-password");
        user.setEmailVerified(true);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded-password"))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> authenticationService.login(request));
    }

    // Email not verified
    @Test
    void shouldThrowExceptionWhenEmailNotVerified() {

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setPassword("encoded-password");
        user.setEmailVerified(false);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encoded-password"))
                .thenReturn(true);

        assertThrows(ValidationException.class,
                () -> authenticationService.login(request));
    }

    // Tests for verifyEmail()
    @Test
    void shouldVerifyEmailSuccessfully() {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("123456");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        authenticationService.verifyEmail(request);

        assertTrue(user.getEmailVerified());
        verify(userRepository).save(user);
    }

    // Invalid code
    @Test
    void shouldThrowExceptionWhenCodeIsInvalid() {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("999999");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> authenticationService.verifyEmail(request));
    }

    // Expired code
    @Test
    void shouldThrowExceptionWhenVerificationCodeExpired() {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("123456");

        AuthUser user = new AuthUser();
        user.setEmail("test@test.com");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> authenticationService.verifyEmail(request));
    }

    // Test: user does not exist in verifyEmail
    @Test
    void shouldThrowExceptionWhenUserNotFoundForVerification() {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("123456");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> authenticationService.verifyEmail(request));
    }
}
