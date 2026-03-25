package com.digitalmoneyhouse.userservice.service.impl;

import com.digitalmoneyhouse.userservice.client.AccountClient;
import com.digitalmoneyhouse.userservice.client.AuthClient;
import com.digitalmoneyhouse.userservice.dto.*;
import com.digitalmoneyhouse.userservice.entity.BlacklistedToken;
import com.digitalmoneyhouse.userservice.entity.Role;
import com.digitalmoneyhouse.userservice.entity.User;
import com.digitalmoneyhouse.userservice.exception.RoleNotFoundException;
import com.digitalmoneyhouse.userservice.exception.ServiceUnavailableException;
import com.digitalmoneyhouse.userservice.repository.BlacklistedTokenRepository;
import com.digitalmoneyhouse.userservice.repository.RoleRepository;
import com.digitalmoneyhouse.userservice.repository.UserRepository;
import com.digitalmoneyhouse.userservice.service.UserService;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountClient accountClient;
    private final PasswordEncoder passwordEncoder;
    private final AuthClient authClient;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AccountClient accountClient,
                       PasswordEncoder passwordEncoder,
                       AuthClient authClient,
                           BlacklistedTokenRepository blacklistedTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.accountClient = accountClient;
        this.passwordEncoder = passwordEncoder;
        this.authClient = authClient;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email is already registered");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER not configured"));

        //Step 1: Save User
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        String code = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Step 2: Create account with compensation
        AccountResponseDto account;
        try {
            AccountCreateRequestDto accountRequest = new AccountCreateRequestDto(savedUser.getId());
            account = accountClient.createAccount(accountRequest);
        } catch (Exception e) {
            // COMPENSATION: Revert saved user
            userRepository.delete(savedUser);
            throw new ServiceUnavailableException("Account service unavailable. Registration rolled back.");
        }

        //Step 3: Send email (not critical, does not require account/user rollback)
        try {
            authClient.sendVerificationEmail(
                    new VerificationEmailRequestDto(savedUser.getEmail(), code)
            );
        } catch (Exception e) {
            log.warn("Email service unavailable for user {}. Email pending.", savedUser.getEmail());
        }

        return new RegisterResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
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

}
