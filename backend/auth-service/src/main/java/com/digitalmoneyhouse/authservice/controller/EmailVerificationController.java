package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.VerificationEmailRequestDto;
import com.digitalmoneyhouse.authservice.entity.AuthUser;
import com.digitalmoneyhouse.authservice.exception.UserNotFoundException;
import com.digitalmoneyhouse.authservice.repository.UserRepository;
import com.digitalmoneyhouse.authservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(
            @Valid @RequestBody VerificationEmailRequestDto request) {

        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationCode());
        return ResponseEntity.ok("Verification email sent");
    }
}
