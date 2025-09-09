package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.jwt.JwtTokenProvider;
import com.rookies4.MiniProject2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.rookies4.MiniProject2.domain.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.SignUpResponse signup(AuthDto.SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .birthdate(request.getBirthdate())
                .profileImageUrl(request.getProfileImageUrl())
                .role(Role.USER)
                .build();
        User savedUser = userRepository.save(user);

        return AuthDto.SignUpResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .nickname(savedUser.getNickname())
                .build();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateToken(user);

        long expiresIn = jwtTokenProvider.getJWT_EXPIRATION_MS();

        return AuthDto.TokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .build();
    }
}