package com.digitalmoneyhouse.userservice.repository;

import com.digitalmoneyhouse.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return true if email exists")
    void shouldReturnTrueIfEmailExists() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("123456");
        user.setEmailVerified(true);

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@mail.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if email does not exist")
    void shouldReturnFalseIfEmailDoesNotExist() {

        boolean exists = userRepository.existsByEmail("notfound@mail.com");

        assertThat(exists).isFalse();
    }
}
