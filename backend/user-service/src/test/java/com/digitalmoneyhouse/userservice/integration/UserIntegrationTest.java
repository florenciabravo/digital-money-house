package com.digitalmoneyhouse.userservice.integration;

import com.digitalmoneyhouse.userservice.client.AccountClient;
import com.digitalmoneyhouse.userservice.client.AuthClient;
import com.digitalmoneyhouse.userservice.dto.AccountResponseDto;
import com.digitalmoneyhouse.userservice.dto.AuthUserResponseDto;
import com.digitalmoneyhouse.userservice.dto.RegisterRequestDto;
import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;
import com.digitalmoneyhouse.userservice.repository.UserRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AccountClient accountClient;

    @MockitoBean
    private AuthClient authClient;

    @Test
    void shouldRegisterUserSuccessfully() {

        // mock auth-service
        AuthUserResponseDto authResponse =
                new AuthUserResponseDto(1L, "integration@test.com");

        when(authClient.register(any()))
                .thenReturn(authResponse);

        // mock account-service
        AccountResponseDto account = new AccountResponseDto();
        account.setCvu("123456789");
        account.setAlias("alias.test");

        when(accountClient.createAccount(any()))
                .thenReturn(account);

        doNothing().when(authClient)
                .sendVerificationEmail(any());

        // Arrange
        String url = "http://localhost:" + port + "/users/register";

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("integration@test.com");
        request.setPassword("password123");
        request.setFirstName("Integration");
        request.setLastName("Test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequestDto> httpRequest =
                new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<RegisterResponseDto> response =
                restTemplate.postForEntity(
                        url,
                        httpRequest,
                        RegisterResponseDto.class
                );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        // validate that user was saved
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {

        // This is now handled by auth-service.
        when(authClient.register(any()))
                .thenThrow(mock(FeignException.Conflict.class));

        String url = "http://localhost:" + port + "/users/register";

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("duplicate@test.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequestDto> httpRequest =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, httpRequest, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldRollbackUserWhenAccountServiceFails() {

        AuthUserResponseDto authResponse =
                new AuthUserResponseDto(1L, "rollback@test.com");

        when(authClient.register(any()))
                .thenReturn(authResponse);

        when(accountClient.createAccount(any()))
                .thenThrow(new RuntimeException("account service down"));

        String url = "http://localhost:" + port + "/users/register";

        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("rollback@test.com");
        request.setPassword("password123");
        request.setFirstName("Rollback");
        request.setLastName("Test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequestDto> httpRequest =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, httpRequest, String.class);

        // Should Return Error
        assertThat(response.getStatusCode().is5xxServerError()).isTrue();

        // Verify that the user was NOT saved
        assertThat(userRepository.count()).isEqualTo(0);
    }
}
