// src/main/java/com/rookies4/MiniProject2/service/AuthService.java
package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.jwt.JwtTokenProvider;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j; // [추가] Slf4j import

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.SignUpResponse signup(AuthDto.SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User userToSave = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .birthdate(request.getBirthdate())
                .profileImageUrl(request.getProfileImageUrl())
                .role(Role.USER) // 기본 역할은 USER
                .build();

        // [수정] save() 메서드가 반환하는 영속화된 User 객체를 받습니다.
        User savedUser = userRepository.save(userToSave);

        // [수정] id가 부여된 savedUser 객체를 사용하여 응답을 생성합니다.
        return AuthDto.SignUpResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .nickname(savedUser.getNickname())
                .build();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AuthDto.TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

        return tokenResponse;
    }
}