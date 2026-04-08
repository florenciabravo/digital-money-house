package com.digitalmoneyhouse.userservice.controller;

import com.digitalmoneyhouse.userservice.dto.RegisterResponseDto;
import com.digitalmoneyhouse.userservice.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.verify;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // Test of the /users/register endpoint
    @Test
    void shouldRegisterUser() throws Exception {

        RegisterResponseDto response = new RegisterResponseDto(
                1L,
                "flor@test.com",
                "123456789",
                "alias.test"
        );

        when(userService.register(any()))
                .thenReturn(response);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "firstName": "Flor",
                  "lastName": "Bravo",
                  "email": "flor@test.com",
                  "password": "123456"
                }
            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("flor@test.com"))
                .andExpect(jsonPath("$.cvu").value("123456789"))
                .andExpect(jsonPath("$.alias").value("alias.test"));

        verify(userService).register(any());
    }

    // Test of the /users/logout endpoint
    @Test
    void shouldLogoutSuccessfully() throws Exception {

        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer token123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        verify(userService).logout("token123");
    }

    // Test logout without token
    @Test
    void shouldReturnBadRequestWhenTokenMissing() throws Exception {

        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isBadRequest());
    }

    //Test of the /users/blacklist/exists endpoint
    @Test
    void shouldReturnTrueWhenTokenIsBlacklisted() throws Exception {

        when(userService.isTokenBlacklisted("token123"))
                .thenReturn(true);

        mockMvc.perform(get("/users/blacklist/exists")
                        .param("token", "token123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

}
