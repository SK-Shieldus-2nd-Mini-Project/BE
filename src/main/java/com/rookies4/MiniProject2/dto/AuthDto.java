package com.rookies4.MiniProject2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Builder;
import lombok.*;
import java.time.LocalDate;

public class AuthDto {
    @Getter
    @Builder // [추가] 테스트에서 객체 생성을 쉽게 하기 위해 Builder 추가
    @NoArgsConstructor(access = AccessLevel.PROTECTED) // [추가] JPA 프록시 등을 위한 기본 생성자
    @AllArgsConstructor // [추가] Builder를 위한 모든 필드를 받는 생성자
    public static class SignUpRequest {
        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        private String username;
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        private String password;
        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        private String nickname;
        @NotNull(message = "생년월일은 필수 입력 항목입니다.")
        private LocalDate birthdate;
        private String profileImageUrl;
    }

    @Getter
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }
    
    @Getter
    public static class SignUpResponse {
        private Long userId;
        private String username;
        private String nickname;

        @Builder
        public SignUpResponse(Long userId, String username, String nickname) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
        }
    }

    @Getter
    public static class TokenResponse {
        private String grantType = "Bearer";
        private String accessToken;
        private long expiresIn;

        @Builder
        public TokenResponse(String accessToken, long expiresIn) {
            this.accessToken = accessToken;
            this.expiresIn = expiresIn;
        }
    }
}