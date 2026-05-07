package com.digitalmoneyhouse.userservice.client;

import com.digitalmoneyhouse.userservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.userservice.dto.AccountProfileDto;
import com.digitalmoneyhouse.userservice.dto.AccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/accounts")
    AccountResponseDto createAccount(@RequestBody AccountCreateRequestDto request);

    @GetMapping("/accounts/user/{userId}")
    AccountProfileDto getAccountByUserId(
            @PathVariable("userId") Long userId,
            @RequestHeader("X-User-Id") Long requestingUserId);
}
