package com.digitalmoneyhouse.userservice.service.impl;

import com.digitalmoneyhouse.userservice.client.AccountClient;
import com.digitalmoneyhouse.userservice.client.AuthClient;
import com.digitalmoneyhouse.userservice.dto.*;
import com.digitalmoneyhouse.userservice.entity.BlacklistedToken;
import com.digitalmoneyhouse.userservice.entity.User;
import com.digitalmoneyhouse.userservice.exception.ServiceUnavailableException;
import com.digitalmoneyhouse.userservice.exception.UnauthorizedException;
import com.digitalmoneyhouse.userservice.exception.UserNotFoundException;
import com.digitalmoneyhouse.userservice.repository.BlacklistedTokenRepository;
import com.digitalmoneyhouse.userservice.repository.UserRepository;
import com.digitalmoneyhouse.userservice.service.UserService;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.FeignException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountClient accountClient;
    private final AuthClient authClient;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
                       AccountClient accountClient,
                       AuthClient authClient,
                           BlacklistedTokenRepository blacklistedTokenRepository) {
        this.userRepository = userRepository;
        this.accountClient = accountClient;
        this.authClient = authClient;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {

        // Step 1: create user in auth-service
        AuthRegisterRequestDto authRequest =
                new AuthRegisterRequestDto(request.getEmail(), request.getPassword());

        AuthUserResponseDto authUser;

        try {
            authUser = authClient.register(authRequest);

        } catch (FeignException.BadRequest e) {
            throw new ValidationException("Invalid user data");

        } catch (FeignException.Conflict e) {
            throw new ValidationException("Email already registered");

        } catch (FeignException e) {
            throw new ServiceUnavailableException("Auth service unavailable");
        }

        // Step 2: save profile in user-service
        User user = new User();
        user.setId(authUser.getId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);

        // Step 3: Create account with compensation
        AccountResponseDto account;
        try {
            AccountCreateRequestDto accountRequest = new AccountCreateRequestDto(savedUser.getId());
            account = accountClient.createAccount(accountRequest);
        } catch (Exception e) {
            // COMPENSATION: Revert saved user in user-service
            userRepository.delete(savedUser);
            // rollback en auth-service
            try {
                authClient.deleteUser(authUser.getId());
            } catch (Exception ex) {
                log.error("Error rolling back user in auth-service. ID: {}", authUser.getId());
            }
            throw new ServiceUnavailableException("Account service unavailable. Registration rolled back.");
        }

        //Step 4: Send email (not critical, does not require account/user rollback)
        try {
            authClient.sendVerificationEmail(
                    new VerificationEmailRequestDto(authUser.getEmail())
            );
        } catch (Exception e) {
            log.warn("Email service unavailable for user {}. Email pending.", authUser.getEmail());
        }

        return new RegisterResponseDto(
                savedUser.getId(),
                authUser.getEmail(),
                account.getCvu(),
                account.getAlias()
        );
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new ValidationException("Token is empty");
        }

        if (blacklistedTokenRepository.existsByToken(token)) {
            return;
        }

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setBlacklistedAt(LocalDateTime.now());

        blacklistedTokenRepository.save(blacklistedToken);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            throw new ValidationException("Token is empty");
        }

        return blacklistedTokenRepository.existsByToken(token);
    }

    @Override
    public UserProfileResponseDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        AccountProfileDto account;
        try {
            account = accountClient.getAccountByUserId(userId, userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("Account not found for userId: " + userId);
        } catch (FeignException.Forbidden e) {
            throw new UnauthorizedException("Access denied");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Account service unavailable");
        }
        return new UserProfileResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                account.getCvu(),
                account.getAlias()
        );
    }

    @Override
    public UserProfileResponseDto updateUserProfile(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        AccountProfileDto account;
        try {
            account = accountClient.getAccountByUserId(userId, userId);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Account service unavailable");
        }

        return new UserProfileResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                account.getCvu(),
                account.getAlias()
        );
    }
}
