// src/main/java/com/rookies4/MiniProject2/service/AuthService.java
package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.RefreshToken;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.jwt.JwtTokenProvider;
import com.rookies4.MiniProject2.repository.RefreshTokenRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public AuthDto.SignUpResponse signup(AuthDto.SignUpRequest request, MultipartFile file) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String profileImageUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // 폴더가 없으면 생성
                }

                // 1. 고유한 파일 이름 생성
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String savedFilename = UUID.randomUUID().toString() + extension;

                // 2. 파일을 지정된 경로에 저장
                Path destinationPath = uploadPath.resolve(savedFilename);
                file.transferTo(destinationPath.toFile());

                // 3. 웹에서 접근 가능한 URL 생성
                profileImageUrl = "/images/" + savedFilename;

            } catch (IOException e) {
                log.error("파일 업로드 중 오류 발생", e);
                // 파일 처리 중 에러가 발생하면 null로 처리하거나 예외를 던질 수 있습니다.
            }
        }

        User userToSave = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .birthdate(request.getBirthdate())
                .profileImageUrl(profileImageUrl)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(userToSave);

        return AuthDto.SignUpResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .nickname(savedUser.getNickname())
                .role(savedUser.getRole())
                .build();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AuthDto.TokenResponse tokenResponse = jwtTokenProvider.generateTokens(authentication);

        // 4. Refresh Token을 DB에 저장 (또는 업데이트)
        RefreshToken refreshToken = RefreshToken.builder()
                .username(authentication.getName())
                .tokenValue(tokenResponse.getRefreshToken())
                .build();

        refreshTokenRepository.findByUsername(authentication.getName())
                .ifPresentOrElse(
                        existingToken -> existingToken.updateToken(refreshToken.getTokenValue()),
                        () -> refreshTokenRepository.save(refreshToken)
                );

        return tokenResponse;
    }
}
