package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.AuthenticationRequestDto;
import com.digitalmoneyhouse.authservice.dto.VerifyEmailRequestDto;
import com.digitalmoneyhouse.authservice.service.AuthenticationService;
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
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequestDto request) {
        authenticationService.verifyEmail(request);
        return ResponseEntity.ok("Email verified successfully");
    }
}
