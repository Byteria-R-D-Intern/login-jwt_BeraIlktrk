package com.staj.login_jwt.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.staj.login_jwt.domain.entity.User;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
} 