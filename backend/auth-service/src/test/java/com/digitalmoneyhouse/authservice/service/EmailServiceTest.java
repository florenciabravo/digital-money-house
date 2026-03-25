package com.digitalmoneyhouse.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    // Send reset password email
    @Test
    void shouldSendResetPasswordEmail() {

        String to = "test@test.com";
        String link = "http://localhost/reset-password?token=abc";

        emailService.sendResetPasswordEmail(to, link);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals(to, message.getTo()[0]);
        assertTrue(message.getSubject().contains("Reset your password"));
        assertTrue(message.getText().contains(link));
    }

    // Send verification email
    @Test
    void shouldSendVerificationEmail() {

        String to = "test@test.com";
        String code = "123456";

        emailService.sendVerificationEmail(to, code);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals(to, message.getTo()[0]);
        assertTrue(message.getSubject().contains("Verify your email"));
        assertTrue(message.getText().contains(code));
    }
}
