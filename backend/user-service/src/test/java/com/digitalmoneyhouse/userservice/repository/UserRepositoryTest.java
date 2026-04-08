package com.digitalmoneyhouse.userservice.repository;

import com.digitalmoneyhouse.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by id")
    void shouldSaveAndFindUserById() {

        Long authUserId = 1L;

        User user = new User();
        user.setId(authUserId);
        user.setFirstName("Flor");
        user.setLastName("Bravo");

        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Flor");
    }
}
