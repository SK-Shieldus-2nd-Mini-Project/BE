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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에 등록된 Bean을 주입 받음
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public AuthDto.SignUpResponse signup(AuthDto.SignUpRequest requestDto) {
        // 사용자 ID(username) 중복 확인
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("[ERROR] 이미 사용 중인 아이디입니다.");
        }

        // 닉네입 중복 확인
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("[ERROR] 이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // DTO를 User 엔티티로 변환
        User newUser = User.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .birthdate(requestDto.getBirthdate())
                .profileImageUrl(requestDto.getProfileImageUrl())
                .role(Role.USER) // 회원가입 시 기본 권한을 USER로 설정
                .build();

        // UserRepository를 통해 DB에 사용자 정보 저장
        User savedUser = userRepository.save(newUser);

        // 저장된 User 엔티티를 SignUpResponse DTO로 변환 및 반환
        return AuthDto.SignUpResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .nickname(savedUser.getNickname())
                .build();
    }

    // 로그인
    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest requestDto) {
        // login ID/PW 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());

        // 사용자 비밀번호 체크
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보 기반으로 JWT 토큰 생성 및 반환
        return jwtTokenProvider.generateToken(authentication);
    }
}
