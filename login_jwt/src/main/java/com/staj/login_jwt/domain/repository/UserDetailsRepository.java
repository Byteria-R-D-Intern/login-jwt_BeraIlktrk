package com.staj.login_jwt.domain.repository;

import java.util.Optional;

import com.staj.login_jwt.domain.entity.UserDetails;

public interface UserDetailsRepository {
    Optional<UserDetails> findByUserId(Long userId);
    UserDetails save(UserDetails userDetails);
    void deleteByUserId(Long userId);
} 