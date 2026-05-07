package com.digitalmoneyhouse.userservice.controller;

import com.digitalmoneyhouse.userservice.dto.RegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;
import com.digitalmoneyhouse.userservice.dto.UserProfileResponseDto;
import com.digitalmoneyhouse.userservice.dto.UserUpdateRequestDto;
import com.digitalmoneyhouse.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requestingUserId) {

        if (!id.equals(requestingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserProfileResponseDto response = userService.getUserProfile(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requestingUserId,
            @Valid @RequestBody UserUpdateRequestDto request) {

        if (!id.equals(requestingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }
}
