package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.service.AuthService; // AuthService import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    // AuthService 주입 (final 키워드 추가)
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignUpResponse> signup(@Valid @RequestBody AuthDto.SignUpRequest request) {
        AuthDto.SignUpResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}