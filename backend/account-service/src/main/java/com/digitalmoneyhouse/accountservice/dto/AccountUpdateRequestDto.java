package com.digitalmoneyhouse.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountUpdateRequestDto {

    @NotBlank(message = "Alias is required")
    private String alias;
}
