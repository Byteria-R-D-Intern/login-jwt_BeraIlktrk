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

import com.staj.login_jwt.domain.entity.User;
import com.staj.login_jwt.domain.entity.UserDetails;
import com.staj.login_jwt.domain.service.UserDetailsService;
import com.staj.login_jwt.domain.service.UserService;
import com.staj.login_jwt.presentation.dto.UserDetailsDto;
import com.staj.login_jwt.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Kullanıcı Detayları ve Rol Yönetimi", description = "Kullanıcı detayları ve rol işlemleri için endpointler")
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
    
    @Operation(
        summary = "Kullanıcı detaylarını getir (hibrit)",
        description = "Kullanıcı kendi detayını görebilir. Admin ise userId parametresi ile başka bir kullanıcının detayını da görebilir. Authorization header'ında geçerli bir JWT token olmalıdır."
    )
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(
        @Parameter(description = "Bearer JWT token içeren Authorization header") @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(description = "(Sadece admin için) Detayları görüntülenecek kullanıcının userId'si") @org.springframework.web.bind.annotation.RequestParam(required = false) Long userId
    ) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            String role = jwtUtil.extractUserRole(token);
            Long tokenUserId = jwtUtil.extractUserId(token);
            Long targetUserId = ("admin".equals(role) && userId != null) ? userId : tokenUserId;
            if (!"admin".equals(role) && userId != null && !userId.equals(tokenUserId)) {
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin başka kullanıcı için detay görüntüleyebilir.");
            }
            UserDetails userDetails = userDetailsService.getUserDetails(targetUserId);
            if (userDetails == null) {
                return ResponseEntity.status(404).body("User details not found");
            }
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Kullanıcı detayları oluştur (hibrit)",
        description = "Kullanıcı kendi detayını ekleyebilir. Admin ise userId parametresi ile başka bir kullanıcı için de detay ekleyebilir. Authorization header'ında geçerli bir JWT token olmalıdır."
    )
    @PostMapping("/details")
    public ResponseEntity<?> createUserDetails(
        @Parameter(description = "Bearer JWT token içeren Authorization header") @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(description = "Kullanıcı detay bilgileri DTO") @Valid @RequestBody UserDetailsDto userDetailsDto,
        @Parameter(description = "(Sadece admin için) Detay eklenecek kullanıcının userId'si") @org.springframework.web.bind.annotation.RequestParam(required = false) Long userId
    ) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            String role = jwtUtil.extractUserRole(token);
            Long tokenUserId = jwtUtil.extractUserId(token);
            Long targetUserId = ("admin".equals(role) && userId != null) ? userId : tokenUserId;
            if (!"admin".equals(role) && userId != null && !userId.equals(tokenUserId)) {
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin başka kullanıcı için detay ekleyebilir.");
            }
            UserDetails existing = userDetailsService.getUserDetails(targetUserId);
            if (existing != null) {
                return ResponseEntity.status(409).body("User details already exist");
            }
            UserDetails userDetails = new UserDetails();
            userDetails.setUserId(targetUserId);
            userDetails.setAddress(userDetailsDto.getAddress());
            userDetails.setPhoneNumber(userDetailsDto.getPhoneNumber());
            userDetails.setBirthDate(userDetailsDto.getBirthDate() != null ? userDetailsDto.getBirthDate().toString() : null);
            UserDetails saved = userDetailsService.saveUserDetails(userDetails);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Kullanıcı detaylarını güncelle (hibrit)",
        description = "Kullanıcı kendi detayını güncelleyebilir. Admin ise userId parametresi ile başka bir kullanıcı için de detay güncelleyebilir. Authorization header'ında geçerli bir JWT token olmalıdır."
    )
    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(
        @Parameter(description = "Bearer JWT token içeren Authorization header") @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(description = "Güncellenecek kullanıcı detay bilgileri DTO") @Valid @RequestBody UserDetailsDto userDetailsDto,
        @Parameter(description = "(Sadece admin için) Detay güncellenecek kullanıcının userId'si") @org.springframework.web.bind.annotation.RequestParam(required = false) Long userId
    ) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            String role = jwtUtil.extractUserRole(token);
            Long tokenUserId = jwtUtil.extractUserId(token);
            Long targetUserId = ("admin".equals(role) && userId != null) ? userId : tokenUserId;
            if (!"admin".equals(role) && userId != null && !userId.equals(tokenUserId)) {
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin başka kullanıcı için detay güncelleyebilir.");
            }
            UserDetails existing = userDetailsService.getUserDetails(targetUserId);
            if (existing == null) {
                return ResponseEntity.status(404).body("User details not found");
            }
            existing.setAddress(userDetailsDto.getAddress());
            existing.setPhoneNumber(userDetailsDto.getPhoneNumber());
            existing.setBirthDate(userDetailsDto.getBirthDate() != null ? userDetailsDto.getBirthDate().toString() : null);
            UserDetails updated = userDetailsService.saveUserDetails(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Kullanıcı rolünü güncelle (admin)",
        description = "Sadece admin kullanıcılar, başka bir kullanıcının rolünü güncelleyebilir. Authorization header'ında geçerli bir JWT token olmalıdır."
    )
    @PutMapping("/role")
    public ResponseEntity<?> updateUserRole(
        @Parameter(description = "Bearer JWT token içeren Authorization header") @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(description = "Kullanıcı adı ve yeni rol bilgisi") @RequestBody UpdateUserRoleRequest request
    ) {
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
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Kullanıcıyı sil (admin)",
        description = "Sadece admin kullanıcılar, başka bir kullanıcının tüm detaylarını ve hesabını silebilir. Authorization header'ında geçerli bir JWT token olmalıdır."
    )
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUserByAdmin(
        @Parameter(description = "Bearer JWT token içeren Authorization header") @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(description = "Silinecek kullanıcının userId'si") @org.springframework.web.bind.annotation.PathVariable Long userId
    ) {
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
                return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin kullanıcılar kullanıcı silebilir.");
            }
            // Kullanıcıyı bul
            User user = userService.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body("Kullanıcı bulunamadı");
            }
            // Önce user details silinsin (varsa)
            userDetailsService.deleteUserDetails(userId);
            // Sonra user silinsin
            userService.deleteUser(userId);
            return ResponseEntity.ok("Kullanıcı ve detayları başarıyla silindi.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }
} 