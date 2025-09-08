package com.rookies4.MiniProject2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Builder;
import java.time.LocalDate;

public class AuthDto {
    @Getter
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