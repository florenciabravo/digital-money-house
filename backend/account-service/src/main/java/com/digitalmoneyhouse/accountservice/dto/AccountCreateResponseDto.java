package com.digitalmoneyhouse.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountCreateResponseDto {
    private Long accountId;
    private String cvu;
    private String alias;
}
