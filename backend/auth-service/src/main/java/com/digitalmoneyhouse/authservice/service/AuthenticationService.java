package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.dto.*;
import com.digitalmoneyhouse.authservice.entity.Role;
import com.digitalmoneyhouse.authservice.entity.AuthUser;
import com.digitalmoneyhouse.authservice.exception.RoleNotFoundException;
import com.digitalmoneyhouse.authservice.exception.UserNotFoundException;
import com.digitalmoneyhouse.authservice.repository.RoleRepository;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public AuthUserResponseDto register(AuthRegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email is already registered");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER not configured"));

        String code = String.format("%06d", new Random().nextInt(999999));

        AuthUser user = AuthUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .emailVerified(false)
                .verificationCode(code)
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        AuthUser savedUser = userRepository.save(user);

        return new AuthUserResponseDto(savedUser.getId(), savedUser.getEmail()
        );
    }

    public void deleteUser(Long id) {
        AuthUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto request) {

        AuthUser user = userRepository.findByEmail(request.getEmail())
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
        AuthUser user = userRepository.findByEmail(request.getEmail())
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
