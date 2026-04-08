package com.digitalmoneyhouse.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Boolean emailVerified = false;
    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;
}
