package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.VerificationEmailRequestDto;
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

    private final EmailService emailService;

    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(
            @Valid @RequestBody VerificationEmailRequestDto request) {

        emailService.sendVerificationEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Verification email sent");
    }
}
