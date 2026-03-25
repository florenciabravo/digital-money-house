package com.digitalmoneyhouse.authservice.controller;

import com.digitalmoneyhouse.authservice.dto.VerificationEmailRequestDto;
import com.digitalmoneyhouse.authservice.service.EmailService;
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

@WebMvcTest(EmailVerificationController.class)
@ActiveProfiles("test")
public class EmailVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendVerificationEmail() throws Exception {

        VerificationEmailRequestDto request = new VerificationEmailRequestDto();
        request.setEmail("test@test.com");
        request.setCode("123456");

        mockMvc.perform(post("/auth/send-verification-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification email sent"));

        Mockito.verify(emailService)
                .sendVerificationEmail("test@test.com", "123456");
    }
}
