package com.digitalmoneyhouse.userservice.controller;

import com.digitalmoneyhouse.userservice.dto.RegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;
import com.digitalmoneyhouse.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequestDto request) {

        RegisterResponseDto response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            //@RequestHeader("Authorization") String authHeader) {
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ValidationException("Token not provided");
        }

        String token = authHeader.substring(7);

        userService.logout(token);

        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/blacklist/exists")
    public ResponseEntity<Boolean> isBlacklisted(@RequestParam String token) {
        boolean exists = userService.isTokenBlacklisted(token);
        return ResponseEntity.ok(exists);
    }
}
