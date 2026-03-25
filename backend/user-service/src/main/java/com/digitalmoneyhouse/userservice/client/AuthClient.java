package com.digitalmoneyhouse.userservice.client;

import com.digitalmoneyhouse.userservice.dto.VerificationEmailRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/auth/send-verification-email")
    void sendVerificationEmail(@RequestBody VerificationEmailRequestDto request);
}
