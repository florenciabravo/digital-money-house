package com.digitalmoneyhouse.authservice.repository;

import com.digitalmoneyhouse.authservice.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AuthUser, Long>{
    Optional<AuthUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
