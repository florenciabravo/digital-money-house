package com.digitalmoneyhouse.userservice.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String cvu;
    private String alias;
}
