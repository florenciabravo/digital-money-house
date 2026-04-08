package com.digitalmoneyhouse.userservice.client;

import com.digitalmoneyhouse.userservice.dto.AuthRegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.AuthUserResponseDto;
import com.digitalmoneyhouse.userservice.dto.VerificationEmailRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/auth/register")
    AuthUserResponseDto register(@RequestBody AuthRegisterRequestDto request);

    @DeleteMapping("/auth/users/{id}")
    void deleteUser(@PathVariable Long id);

    @PostMapping("/auth/send-verification-email")
    void sendVerificationEmail(@RequestBody VerificationEmailRequestDto request);
}
