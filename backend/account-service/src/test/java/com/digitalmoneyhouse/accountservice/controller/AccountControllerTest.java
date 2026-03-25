package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.AccountCreateRequestDto;
import com.digitalmoneyhouse.accountservice.dto.AccountCreateResponseDto;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {

        AccountCreateRequestDto request = new AccountCreateRequestDto();

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
