package com.digitalmoneyhouse.userservice.client;

import com.digitalmoneyhouse.userservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.userservice.dto.AccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/accounts")
    AccountResponseDto createAccount(@RequestBody AccountCreateRequestDto request);
}
