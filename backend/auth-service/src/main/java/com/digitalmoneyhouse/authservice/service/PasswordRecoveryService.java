package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.dto.ForgotPasswordRequestDto;
import com.digitalmoneyhouse.authservice.dto.ResetPasswordRequestDto;
import com.digitalmoneyhouse.authservice.entity.PasswordResetToken;
import com.digitalmoneyhouse.authservice.entity.User;
import com.digitalmoneyhouse.authservice.repository.PasswordResetTokenRepository;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${frontend.reset-password-url}")
    private String resetPasswordUrl;

    public void forgotPassword(ForgotPasswordRequestDto request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {

            tokenRepository.deleteByUserId(user.getId());

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expirationDate(LocalDateTime.now().plusMinutes(15))
                    .build();

            tokenRepository.save(resetToken);

            String link = resetPasswordUrl + "?token=" + token;

            emailService.sendResetPasswordEmail(user.getEmail(), link);
        });
    }

    public void resetPassword(ResetPasswordRequestDto request) {

        if (!request.getNewPassword().equals(request.getRepeatPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ValidationException("Invalid token"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
