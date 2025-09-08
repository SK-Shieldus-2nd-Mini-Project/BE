package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.service.AuthService;
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
    private final AuthService authService;

    /**
     * 회원가입 API 엔드포인트
     * @param: requestDto 회원가입 요청 Body
     * @return: 생성된 사용자 정보와 201 Created 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignUpResponse> signup(@Valid @RequestBody AuthDto.SignUpRequest request) {
        AuthDto.SignUpResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        // TODO: authService.login(request) 호출
        // AuthDto.TokenResponse token = authService.login(request);
        // return ResponseEntity.ok(token);
        return null; // 임시
    }
}