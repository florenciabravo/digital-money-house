package com.digitalmoneyhouse.accountservice.dto;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AccountCreateRequestDto {
    @NotNull(message = "UserId is required")
    @Positive(message = "UserId must be positive")
    private Long userId;
}
