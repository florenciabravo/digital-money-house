package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.AccountBalanceResponseDto;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateResponseDto;
import com.digitalmoneyhouse.accountservice.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {

        AccountCreateRequestDto request = new AccountCreateRequestDto();
        request.setUserId(1L);

        AccountCreateResponseDto response =
                new AccountCreateResponseDto(
                        10L,
                        "1234567890123456789012",
                        "casa.sol.arbol"
                );

        when(accountService.createAccount(request)).thenReturn(response);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cvu").value("1234567890123456789012"))
                .andExpect(jsonPath("$.alias").value("casa.sol.arbol"));
    }

    // missing header - 400
    @Test
    void shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {

        AccountCreateRequestDto request = new AccountCreateRequestDto();

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // Case OK (200)
    @Test
    void shouldReturnAccountBalanceSuccessfully() throws Exception {

        AccountBalanceResponseDto response =
                new AccountBalanceResponseDto(1L, BigDecimal.valueOf(1000));

        when(accountService.getBalance(1L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/accounts/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    // Account not found (404)
    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {

        when(accountService.getBalance(1L, 1L))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/accounts/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
