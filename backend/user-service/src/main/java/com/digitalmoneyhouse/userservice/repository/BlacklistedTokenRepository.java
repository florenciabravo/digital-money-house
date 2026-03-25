package com.digitalmoneyhouse.userservice.repository;

import com.digitalmoneyhouse.userservice.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository <BlacklistedToken, Long> {

    boolean existsByToken(String token);
}
