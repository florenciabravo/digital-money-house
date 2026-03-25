package com.digitalmoneyhouse.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountResponseDto {
    private Long accountId;
    private String cvu;
    private String alias;
}
