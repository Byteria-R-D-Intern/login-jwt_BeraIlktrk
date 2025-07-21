package com.staj.login_jwt.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staj.login_jwt.domain.entity.UserDetails;
import com.staj.login_jwt.domain.service.UserDetailsService;
import com.staj.login_jwt.presentation.dto.UserDetailsDto;
import com.staj.login_jwt.util.JwtUtil;
import com.staj.login_jwt.domain.entity.User;
import com.staj.login_jwt.domain.service.UserService;

import jakarta.validation.Valid;

// DTO for role update
class UpdateUserRoleRequest {
    private String username;
    private String newRole;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }
}

@RestController
@RequestMapping("/user")
public class UserDetailsController {
    
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    public UserDetailsController(UserDetailsService userDetailsService, JwtUtil jwtUtil, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Token kontrolü
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            
            String token = authorizationHeader.substring(7);
            
            // Token geçerliliği kontrolü
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            // Rol kontrolü (sadece admin erişebilir)
            String role = jwtUtil.extractUserRole(token);
            if (!"admin".equals(role)) {
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin erişebilir.");
            }
            // Token'dan userId çıkar
            Long userId = jwtUtil.extractUserId(token);
            
            // Kullanıcı detaylarını getir
            UserDetails userDetails = userDetailsService.getUserDetails(userId);
            
            if (userDetails == null) {
                return ResponseEntity.status(404).body("User details not found");
            }
            
            return ResponseEntity.ok(userDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> createUserDetails(@RequestHeader("Authorization") String authorizationHeader,
                                               @Valid @RequestBody UserDetailsDto userDetailsDto) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            Long userId = jwtUtil.extractUserId(token);
            UserDetails existing = userDetailsService.getUserDetails(userId);
            if (existing != null) {
                return ResponseEntity.status(409).body("User details already exist");
            }
            UserDetails userDetails = new UserDetails();
            userDetails.setUserId(userId);
            userDetails.setAddress(userDetailsDto.getAddress());
            userDetails.setPhoneNumber(userDetailsDto.getPhoneNumber());
            userDetails.setBirthDate(userDetailsDto.getBirthDate() != null ? userDetailsDto.getBirthDate().toString() : null);
            UserDetails saved = userDetailsService.saveUserDetails(userDetails);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String authorizationHeader,
                                               @Valid @RequestBody UserDetailsDto userDetailsDto) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            Long userId = jwtUtil.extractUserId(token);
            UserDetails existing = userDetailsService.getUserDetails(userId);
            if (existing == null) {
                return ResponseEntity.status(404).body("User details not found");
            }
            existing.setAddress(userDetailsDto.getAddress());
            existing.setPhoneNumber(userDetailsDto.getPhoneNumber());
            existing.setBirthDate(userDetailsDto.getBirthDate() != null ? userDetailsDto.getBirthDate().toString() : null);
            UserDetails updated = userDetailsService.saveUserDetails(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateUserRole(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody UpdateUserRoleRequest request) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            String role = jwtUtil.extractUserRole(token);
            if (!"admin".equals(role)) {
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin kullanıcılar rol değiştirebilir.");
            }
            // Kullanıcıyı bul
            User user = userService.findByUsername(request.getUsername());
            if (user == null) {
                return ResponseEntity.status(404).body("Kullanıcı bulunamadı");
            }
            user.setRole(request.getNewRole());
            userService.saveUser(user);
            return ResponseEntity.ok("Kullanıcı rolü güncellendi");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/details")
    public ResponseEntity<?> deleteUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            // Admin rol kontrolü kaldırıldı, tüm kullanıcılar kendi detayını silebilir
            Long userId = jwtUtil.extractUserId(token);
            UserDetails existing = userDetailsService.getUserDetails(userId);
            if (existing == null) {
                return ResponseEntity.status(404).body("User details not found");
            }
            userDetailsService.deleteUserDetails(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
} 