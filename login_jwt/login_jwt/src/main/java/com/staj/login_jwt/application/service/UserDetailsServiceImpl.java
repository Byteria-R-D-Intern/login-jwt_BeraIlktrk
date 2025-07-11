package com.staj.login_jwt.application.service;

import org.springframework.stereotype.Service;

import com.staj.login_jwt.domain.entity.UserDetails;
import com.staj.login_jwt.domain.service.UserDetailsService;
import com.staj.login_jwt.infrastructure.repository.JpaUserDetailsRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final JpaUserDetailsRepository userDetailsRepository;
    
    public UserDetailsServiceImpl(JpaUserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }
    
    @Override
    public UserDetails getUserDetails(Long userId) {
        return userDetailsRepository.findByUserId(userId).orElse(null);
    }
    
    @Override
    public UserDetails saveUserDetails(UserDetails userDetails) {
        return userDetailsRepository.save(userDetails);
    }
    
    @Override
    public void deleteUserDetails(Long userId) {
        userDetailsRepository.deleteByUserId(userId);
    }
} 