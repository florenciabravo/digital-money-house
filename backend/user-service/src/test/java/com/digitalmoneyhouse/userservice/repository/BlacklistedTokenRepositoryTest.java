package com.digitalmoneyhouse.userservice.repository;

import com.digitalmoneyhouse.userservice.entity.BlacklistedToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BlacklistedTokenRepositoryTest {

    @Autowired
    private BlacklistedTokenRepository repository;

    @Test
    @DisplayName("Should return true if token is blacklisted")
    void shouldReturnTrueIfTokenExists() {

        BlacklistedToken token = new BlacklistedToken();
        token.setToken("abc123");
        token.setBlacklistedAt(LocalDateTime.now());

        repository.save(token);

        boolean exists = repository.existsByToken("abc123");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if token not blacklisted")
    void shouldReturnFalseIfTokenDoesNotExist() {

        boolean exists = repository.existsByToken("notexists");

        assertThat(exists).isFalse();
    }
}
