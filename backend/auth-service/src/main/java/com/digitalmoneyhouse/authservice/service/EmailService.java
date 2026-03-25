package com.digitalmoneyhouse.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reset your password - Digital Money House");
        message.setText(
                        "Hello!\n\n" +
                        "Click the following link to reset your password:\n" +
                        link + "\n\n" +
                        "⚠ This link expires in 15 minutes.\n\n" +
                        "If you did not request this, please ignore this email."
        );

        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Verify your email - Digital Money House");
        message.setText(
                        "Hello!\n\n" +
                        "Your verification code is:\n " +
                        code + "\n\n" +
                        "⚠ This code expires in 15 minutes.\n\n" +
                        "Enter this code in the application to verify your email address."
        );
        mailSender.send(message);
    }
}
