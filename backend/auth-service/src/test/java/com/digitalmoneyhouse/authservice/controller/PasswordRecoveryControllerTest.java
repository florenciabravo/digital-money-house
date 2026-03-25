package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.ForgotPasswordRequestDto;
import com.digitalmoneyhouse.authservice.dto.ResetPasswordRequestDto;
import com.digitalmoneyhouse.authservice.service.PasswordRecoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordRecoveryController.class)
@ActiveProfiles("test")
public class PasswordRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordRecoveryService passwordRecoveryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendRecoveryEmail() throws Exception {

        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto();
        request.setEmail("test@test.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("If the email exists, a recovery link was sent"));

        Mockito.verify(passwordRecoveryService)
                .forgotPassword(Mockito.any());
    }

    @Test
    void shouldResetPassword() throws Exception {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("token123");
        request.setNewPassword("newPassword123");
        request.setRepeatPassword("newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));

        Mockito.verify(passwordRecoveryService)
                .resetPassword(Mockito.any());
    }

    @Test
    void shouldFailWhenRepeatPasswordIsMissing() throws Exception {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("token123");
        request.setNewPassword("newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Repeat password is required"));
    }
}
