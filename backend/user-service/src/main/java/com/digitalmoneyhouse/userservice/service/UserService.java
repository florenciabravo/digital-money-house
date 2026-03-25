package com.digitalmoneyhouse.userservice.service;

import com.digitalmoneyhouse.userservice.dto.RegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;

public interface UserService {

    RegisterResponseDto register(RegisterRequestDto request);

    void logout(String token);

    boolean isTokenBlacklisted(String token);

}
