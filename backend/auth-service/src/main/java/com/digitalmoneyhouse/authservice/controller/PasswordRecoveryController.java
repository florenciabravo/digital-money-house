package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.ForgotPasswordRequestDto;
import com.digitalmoneyhouse.authservice.dto.ResetPasswordRequestDto;
import com.digitalmoneyhouse.authservice.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        passwordRecoveryService.forgotPassword(request);
        return ResponseEntity.ok("If the email exists, a recovery link was sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        passwordRecoveryService.resetPassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }
}
