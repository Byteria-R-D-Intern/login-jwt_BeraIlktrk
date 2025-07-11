package com.staj.login_jwt.domain.service;

import com.staj.login_jwt.domain.entity.UserDetails;

public interface UserDetailsService {
    UserDetails getUserDetails(Long userId);
    UserDetails saveUserDetails(UserDetails userDetails);
    void deleteUserDetails(Long userId);
} 