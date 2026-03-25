package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.dto.AuthenticationRequestDto;
import com.digitalmoneyhouse.authservice.dto.AuthenticationResponseDto;
import com.digitalmoneyhouse.authservice.dto.VerifyEmailRequestDto;
import com.digitalmoneyhouse.authservice.entity.User;
import com.digitalmoneyhouse.authservice.exception.UserNotFoundException;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponseDto login(AuthenticationRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid password");
        }

        if (!user.getEmailVerified()) {
            throw new ValidationException("Email not verified");
        }

        String jwt = jwtService.generateToken(user);

        return AuthenticationResponseDto.builder()
                .token(jwt)
                .build();
    }

    public void verifyEmail(VerifyEmailRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("User not found"));

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Code expired");
        }

        if (!user.getVerificationCode().equals(request.getCode())) {
            throw new ValidationException("Invalid code");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        userRepository.save(user);
    }
}
