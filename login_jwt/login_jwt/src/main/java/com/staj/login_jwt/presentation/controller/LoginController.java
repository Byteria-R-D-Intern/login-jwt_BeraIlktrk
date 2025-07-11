package com.staj.login_jwt.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.staj.login_jwt.domain.entity.User;
import com.staj.login_jwt.domain.service.UserService;
import com.staj.login_jwt.presentation.dto.LoginRequest;
import com.staj.login_jwt.util.JwtUtil;

@RestController
public class LoginController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Kullanıcı adı veya şifre hatalı!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
        User user = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
        
        if (user != null) {
            return ResponseEntity.ok("Kayıt başarılı!");
        } else {
            return ResponseEntity.status(400).body("Kullanıcı adı zaten kullanılıyor!");
        }
    }
} 