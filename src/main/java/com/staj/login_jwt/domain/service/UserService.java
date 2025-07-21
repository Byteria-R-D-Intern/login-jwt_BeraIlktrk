package com.staj.login_jwt.domain.service;

import java.util.Optional;

import com.staj.login_jwt.domain.entity.User;

public interface UserService {
    User login(String username, String password);
    User registerUser(String username, String password);
    User updateUser(Long userId, User updatedUser);
    void deleteUser(Long userId);
    Optional<User> findById(Long id);
    User findByUsername(String username);
    User saveUser(User user);
} 