package com.digitalmoneyhouse.userservice.service.impl;

import com.digitalmoneyhouse.userservice.client.AccountClient;
import com.digitalmoneyhouse.userservice.client.AuthClient;
import com.digitalmoneyhouse.userservice.dto.*;
import com.digitalmoneyhouse.userservice.entity.BlacklistedToken;
import com.digitalmoneyhouse.userservice.entity.User;
import com.digitalmoneyhouse.userservice.exception.ServiceUnavailableException;
import com.digitalmoneyhouse.userservice.repository.BlacklistedTokenRepository;
import com.digitalmoneyhouse.userservice.repository.UserRepository;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private AuthClient authClient;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    //Register success
    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequestDto request = new RegisterRequestDto(
                "Flor",
                "Bravo",
                "flor@test.com",
                "123456"
        );

        AuthUserResponseDto authResponse =
                new AuthUserResponseDto(1L, "flor@test.com");

        when(authClient.register(any()))
                .thenReturn(authResponse);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Flor");
        savedUser.setLastName("Bravo");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        AccountResponseDto accountResponse = new AccountResponseDto();
        accountResponse.setCvu("123456789");
        accountResponse.setAlias("alias.test");

        when(accountClient.createAccount(any()))
                .thenReturn(accountResponse);

        RegisterResponseDto response = userServiceImpl.register(request);

        assertEquals("flor@test.com", response.getEmail());
        assertEquals("123456789", response.getCvu());
        assertEquals("alias.test", response.getAlias());

        verify(userRepository).save(any(User.class));
        verify(accountClient).createAccount(any());
        verify(authClient).sendVerificationEmail(any());
    }

    // Logout success
    @Test
    void shouldBlacklistTokenOnLogout() {

        String token = "jwt_token";

        when(blacklistedTokenRepository.existsByToken(token))
                .thenReturn(false);

        userServiceImpl.logout(token);

        verify(blacklistedTokenRepository)
                .save(any(BlacklistedToken.class));
    }

    // Logout token already blacklisted
    @Test
    void shouldNotSaveTokenIfAlreadyBlacklisted() {

        String token = "jwt_token";

        when(blacklistedTokenRepository.existsByToken(token))
                .thenReturn(true);

        userServiceImpl.logout(token);

        verify(blacklistedTokenRepository, never())
                .save(any(BlacklistedToken.class));
    }

    // Logout token empty
    @Test
    void shouldThrowExceptionWhenTokenIsEmpty() {

        assertThrows(ValidationException.class,
                () -> userServiceImpl.logout(""));
    }

    // Check blacklist
    @Test
    void shouldReturnTrueWhenTokenIsBlacklisted() {

        String token = "jwt_token";

        when(blacklistedTokenRepository.existsByToken(token))
                .thenReturn(true);

        boolean result = userServiceImpl.isTokenBlacklisted(token);

        assertTrue(result);
    }

    // account-service fails → rollback user + auth
    @Test
    void shouldRollbackUserWhenAccountServiceFails() {

        RegisterRequestDto request = new RegisterRequestDto(
                "Flor",
                "Bravo",
                "flor@test.com",
                "123456"
        );

        AuthUserResponseDto authResponse =
                new AuthUserResponseDto(1L, "flor@test.com");

        when(authClient.register(any()))
                .thenReturn(authResponse);

        User savedUser = new User();
        savedUser.setId(1L);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(accountClient.createAccount(any()))
                .thenThrow(new RuntimeException("account service down"));

        assertThrows(ServiceUnavailableException.class,
                () -> userServiceImpl.register(request));

        verify(userRepository).delete(savedUser);
        verify(accountClient).createAccount(any());
    }

    // Email fails but registration continues
    @Test
    void shouldRegisterUserEvenIfEmailServiceFails() {

        RegisterRequestDto request = new RegisterRequestDto(
                "Flor",
                "Bravo",
                "flor@test.com",
                "123456"
        );

        AuthUserResponseDto authResponse =
                new AuthUserResponseDto(1L, "flor@test.com");

        when(authClient.register(any()))
                .thenReturn(authResponse);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Flor");
        savedUser.setLastName("Bravo");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        AccountResponseDto account = new AccountResponseDto();
        account.setCvu("123456");
        account.setAlias("alias.test");

        when(accountClient.createAccount(any()))
                .thenReturn(account);

        doThrow(new RuntimeException("email service down"))
                .when(authClient)
                .sendVerificationEmail(any());

        RegisterResponseDto response = userServiceImpl.register(request);

        assertNotNull(response);
        assertEquals("flor@test.com", response.getEmail());

        verify(userRepository).save(any(User.class));
        verify(accountClient).createAccount(any());
        verify(authClient).sendVerificationEmail(any());
    }
}
