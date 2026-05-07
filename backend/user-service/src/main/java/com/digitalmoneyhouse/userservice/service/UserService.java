package com.digitalmoneyhouse.userservice.service;

import com.digitalmoneyhouse.userservice.dto.RegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;
import com.digitalmoneyhouse.userservice.dto.UserProfileResponseDto;
import com.digitalmoneyhouse.userservice.dto.UserUpdateRequestDto;

public interface UserService {

    RegisterResponseDto register(RegisterRequestDto request);

    void logout(String token);

    boolean isTokenBlacklisted(String token);

    UserProfileResponseDto getUserProfile(Long userId);

    UserProfileResponseDto updateUserProfile(Long userId, UserUpdateRequestDto request);

}
