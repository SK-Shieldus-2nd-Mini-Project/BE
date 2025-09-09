package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.service.AuthService; // 이 부분을 추가
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
    private final AuthService authService; // TODO 제거 및 실제 주입

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