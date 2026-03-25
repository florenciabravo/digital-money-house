package com.digitalmoneyhouse.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateRequestDto {

    @NotNull(message = "UserId is required")
    private Long userId;
}
