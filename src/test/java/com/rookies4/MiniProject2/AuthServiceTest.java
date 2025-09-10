package com.rookies4.MiniProject2;


import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.repository.UserRepository;
import com.rookies4.MiniProject2.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입을 성공적으로 완료한다")
    void signup_Success() {
        // given (주어진 상황)
        // [수정] @Builder를 사용하여 테스트용 DTO를 깔끔하게 생성
        AuthDto.SignUpRequest request = AuthDto.SignUpRequest.builder()
                .username("testuser")
                .password("password123!")
                .nickname("testnick")
                .birthdate(LocalDate.of(2000, 1, 1))
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByNickname(request.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L) // DB에서 ID가 생성되었다고 가정
                .username(request.getUsername())
                .nickname(request.getNickname())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when (무엇을 할 때)
        AuthDto.SignUpResponse response = authService.signup(request, null);

        // then (결과 확인)
        assertThat(response).isNotNull(); // 응답이 null이 아닌지 확인
        assertThat(response.getUserId()).isEqualTo(1L); // ID가 정상적으로 반환되었는지 확인
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("중복된 아이디로 회원가입 시 예외가 발생한다")
    void signup_Fail_DuplicateUsername() {
        // given
        AuthDto.SignUpRequest request = AuthDto.SignUpRequest.builder()
                .username("duplicateUser")
                .password("password123!")
                .nickname("anynick")
                .birthdate(LocalDate.of(2000, 1, 1))
                .build();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(request, null);
        });
        assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 아이디입니다.");
    }
}