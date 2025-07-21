package com.staj.login_jwt.domain.repository;

import java.util.Optional;

import com.staj.login_jwt.domain.entity.User;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
} 