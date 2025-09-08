package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.AuthDto;
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
    // private final AuthService authService; // TODO: AuthService 주입

    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignUpResponse> signup(@Valid @RequestBody AuthDto.SignUpRequest request) {
        // TODO: authService.signup(request) 호출
        // AuthDto.SignUpResponse response = authService.signup(request);
        // return ResponseEntity.status(HttpStatus.CREATED).body(response);
        return null; // 임시
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        // TODO: authService.login(request) 호출
        // AuthDto.TokenResponse token = authService.login(request);
        // return ResponseEntity.ok(token);
        return null; // 임시
    }
}