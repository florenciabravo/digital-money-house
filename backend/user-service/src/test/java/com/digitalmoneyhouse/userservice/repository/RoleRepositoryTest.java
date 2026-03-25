package com.digitalmoneyhouse.userservice.repository;

import com.digitalmoneyhouse.userservice.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should find role by name")
    void shouldFindRoleByName() {

        //Role role = new Role();
        //role.setName("ROLE_USER");

        //roleRepository.save(role);

        Optional<Role> result = roleRepository.findByName("ROLE_USER");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should return empty if role not found")
    void shouldReturnEmptyIfRoleNotExists() {

        Optional<Role> result = roleRepository.findByName("ROLE_INEXISTENTE");

        assertThat(result).isEmpty();
    }
}
