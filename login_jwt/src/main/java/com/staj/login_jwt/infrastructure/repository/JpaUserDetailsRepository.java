package com.staj.login_jwt.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.staj.login_jwt.domain.entity.UserDetails;

@Repository
public interface JpaUserDetailsRepository extends JpaRepository<UserDetails, Long> {
    Optional<UserDetails> findByUserId(Long userId);
    void deleteByUserId(Long userId);
} 