package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.Group;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class GroupDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "모임 이름은 필수 입력 항목입니다.")
        private String groupName;

        @NotNull(message = "지역 ID는 필수 입력 항목입니다.")
        private Integer regionId;

        @NotNull(message = "종목 ID는 필수 입력 항목입니다.")
        private Integer sportId;

        private String description;

        @NotNull(message = "최대 인원수는 필수 입력 항목입니다.")
        private int maxMembers;

        @NotNull(message = "모임 시간은 필수 입력 항목입니다.")
        private LocalDateTime meetingTime;

        @NotBlank(message = "모임 장소는 필수 입력 항목입니다.")
        private String location;
    }

    // 메인 화면 모임 목록 응답용 DTO
    @Getter
    @Builder
    public static class GroupListResponse {
        private Long groupId;
        private String groupName;
        private String regionName;
        private String sportName;
        private String leaderNickname;
        private int currentMembers;
        private int maxMembers;
        private LocalDateTime createdAt;
    }
}