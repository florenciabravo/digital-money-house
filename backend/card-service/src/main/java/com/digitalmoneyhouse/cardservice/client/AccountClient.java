package com.digitalmoneyhouse.cardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/accounts/{id}/exists")
    void validateAccountOwnership(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long userId
    );
}
