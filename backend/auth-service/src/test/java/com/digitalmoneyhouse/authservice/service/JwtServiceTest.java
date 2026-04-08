package com.digitalmoneyhouse.authservice.service;

import com.digitalmoneyhouse.authservice.entity.Role;
import com.digitalmoneyhouse.authservice.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "mySuperSecretKeymySuperSecretKey123456"
        );
    }

    // Test: generate token
    @Test
    void shouldGenerateValidToken() {

        Role role = new Role();
        role.setName("USER");

        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(role);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
    }

    // Verify token content
    @Test
    void tokenShouldContainCorrectClaims() {

        Role role = new Role();
        role.setName("USER");

        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(role);

        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey("mySuperSecretKeymySuperSecretKey123456".getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("test@test.com", claims.getSubject());
        assertEquals(1, claims.get("id"));
        assertEquals("USER", claims.get("role"));
    }

    // Verify expiration
    @Test
    void tokenShouldHaveExpiration() {

        Role role = new Role();
        role.setName("USER");

        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(role);

        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey("mySuperSecretKeymySuperSecretKey123456".getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertNotNull(claims.getExpiration());
    }
}
