package com.rookies4.MiniProject2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies4.MiniProject2.config.SecurityConfig;
import com.rookies4.MiniProject2.controller.AuthController;
import com.rookies4.MiniProject2.dto.AuthDto;
import com.rookies4.MiniProject2.jwt.JwtTokenProvider;
import com.rookies4.MiniProject2.service.AuthService;
import com.rookies4.MiniProject2.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// [수정] 테스트에 필요한 SecurityConfig를 명시적으로 Import 합니다.
@Import(SecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // [수정] SecurityConfig가 의존하는 Bean들을 Mock으로 주입해줍니다.
    // 이것이 없으면 테스트 환경을 구성하다가 에러가 발생합니다.
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;


    @Test
    @DisplayName("정상적인 정보로 회원가입을 요청하면 201 Created 상태를 반환한다")
    void signup_Success() throws Exception {
        // given
        AuthDto.SignUpRequest request = AuthDto.SignUpRequest.builder()
                .username("testuser")
                .password("password123!")
                .nickname("testnick")
                .birthdate(LocalDate.of(2000, 1, 1))
                .build();

        AuthDto.SignUpResponse response = AuthDto.SignUpResponse.builder()
                .userId(1L)
                .username("testuser")
                .nickname("testnick")
                .build();
        given(authService.signup(any(AuthDto.SignUpRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("필수 입력값이 없는 회원가입 요청은 400 Bad Request 상태를 반환한다")
    void signup_Fail_InvalidInput() throws Exception {
        // given
        AuthDto.SignUpRequest request = AuthDto.SignUpRequest.builder()
                .nickname("badnick")
                .birthdate(LocalDate.of(2000, 1, 1))
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

