package com.digitalmoneyhouse.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterRequestDto {
    private String email;
    private String password;
}
