package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String nickname;
        private LocalDate birthdate;
        private String profileImageUrl;

        // 비밀번호 필드 추가
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.") // 선택적으로 유효성 검사 추가
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class UserInfoResponse {
        private Long userId;
        private String username;
        private String nickname;
        private LocalDate birthdate;
        private String profileImageUrl;
        private Role role; // role 필드 추가
        private boolean hasCreatedGroup;

        @Builder
        public UserInfoResponse(User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.birthdate = user.getBirthdate();
            this.profileImageUrl = user.getProfileImageUrl();
            this.role = user.getRole(); // user 객체에서 role 정보 할당
            this.hasCreatedGroup = user.getLeadingGroups() != null && !user.getLeadingGroups().isEmpty();
        }
    }

    // 관리자용 회원 정보 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class AdminUpdateRequest {
        private String nickname;
        private LocalDate birthdate;
        private String profileImageUrl;
        private String password;
        private Role role; // 관리자가 역할을 변경할 수 있도록 필드 추가
    }

    //가입 신청자 정보 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ApplicantResponse {
        private Long userId;
        private String nickname;
        private String profileImageUrl;

        @Builder
        public ApplicantResponse(User user) {
            this.userId = user.getId();
            this.nickname = user.getNickname();
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberInfoResponse {
        private Long userId;
        private String nickname;
        private String profileImageUrl;

        @Builder
        public MemberInfoResponse(User user) {
            this.userId = user.getId();
            this.nickname = user.getNickname();
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }
}