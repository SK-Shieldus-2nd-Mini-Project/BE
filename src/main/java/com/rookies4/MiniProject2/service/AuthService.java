package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에 등록된 Bean을 주입 받음

    /**
     * 회원가입 비즈니스 로직을 처리하는 메서드
     * @param: requestDto 회원가입 요청 정보를 담은 DTO
     * @return: 생성된 사용자의 정보를 담은 응답 DTO
     */
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
}
