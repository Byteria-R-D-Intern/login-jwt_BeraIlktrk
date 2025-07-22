package com.staj.login_jwt.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.staj.login_jwt.domain.entity.User;
import com.staj.login_jwt.domain.service.UserService;
import com.staj.login_jwt.presentation.dto.LoginRequest;
import com.staj.login_jwt.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Kullanıcı Girişi ve Kayıt", description = "Kullanıcı login ve kayıt işlemleri için endpointler")
@RestController
public class LoginController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
        summary = "Kullanıcı girişi yap",
        description = "Kullanıcı adı ve şifre ile giriş yapılır. Başarılı olursa JWT token döner. Hatalıysa 401 döner."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(description = "Kullanıcı adı ve şifre bilgileri") @RequestBody LoginRequest loginRequest
    ) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getRole());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Kullanıcı adı veya şifre hatalı!");
        }
    }

    @Operation(
        summary = "Kullanıcı kaydı yap (sadece admin)",
        description = "Yeni bir kullanıcı kaydı oluşturur. Sadece admin kullanıcılar bu işlemi yapabilir. Kullanıcı adı daha önce alınmışsa 400 döner. Admin değilse 403 döner."
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(
        @Parameter(description = "Kayıt için kullanıcı adı ve şifre bilgileri") @RequestBody LoginRequest registerRequest,
        @Parameter(description = "Bearer JWT token içeren Authorization header") @org.springframework.web.bind.annotation.RequestHeader("Authorization") String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Authorization header is missing or invalid");
        }
        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token is invalid or expired");
        }
        String role = jwtUtil.extractUserRole(token);
        if (!"admin".equals(role)) {
            return ResponseEntity.status(403).body("Yetkiniz yok! Sadece admin kullanıcılar yeni kullanıcı kaydı yapabilir.");
        }
        User user = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok("Kullanıcı başarıyla eklendi.");
        } else {
            return ResponseEntity.status(400).body("Kullanıcı adı zaten kullanılıyor.");
        }
    }
} 