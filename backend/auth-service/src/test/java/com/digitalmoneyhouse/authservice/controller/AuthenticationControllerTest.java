package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.AuthenticationRequestDto;
import com.digitalmoneyhouse.authservice.dto.AuthenticationResponseDto;
import com.digitalmoneyhouse.authservice.dto.VerifyEmailRequestDto;
import com.digitalmoneyhouse.authservice.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        AuthenticationResponseDto response = new AuthenticationResponseDto();
        response.setToken("fake-jwt-token");

        Mockito.when(authenticationService.login(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldVerifyEmailSuccessfully() throws Exception {

        VerifyEmailRequestDto request = new VerifyEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("123456");

        mockMvc.perform(post("/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully"));

        Mockito.verify(authenticationService).verifyEmail(Mockito.any());
    }
}
