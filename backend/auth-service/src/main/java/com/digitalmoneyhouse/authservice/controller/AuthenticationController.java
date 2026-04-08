package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.AuthenticationRequestDto;
import com.digitalmoneyhouse.authservice.dto.VerifyEmailRequestDto;
import com.digitalmoneyhouse.authservice.dto.AuthRegisterRequestDto;
import com.digitalmoneyhouse.authservice.dto.AuthUserResponseDto;
import com.digitalmoneyhouse.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserResponseDto> register(
            @Valid @RequestBody AuthRegisterRequestDto request) {

        return ResponseEntity.ok(authenticationService.register(request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authenticationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

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
