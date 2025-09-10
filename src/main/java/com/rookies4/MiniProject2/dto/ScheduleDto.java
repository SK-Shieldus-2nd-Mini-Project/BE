package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.Schedule;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ScheduleDto {

    // 일정 생성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "모임 장소는 필수입니다.")
        private String location;

        @NotNull(message = "모임 시간은 필수입니다.")
        @Future(message = "모임 시간은 현재 시간 이후로만 설정 가능합니다.")
        private LocalDateTime meetingTime;

        private String description;
    }

    // 일정 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String location;
        private LocalDateTime meetingTime;
        private String description;
    }

    // 일정 조회 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class ScheduleResponse {
        private Long scheduleId;
        private String location;
        private LocalDateTime meetingTime;
        private String description;

        @Builder
        public ScheduleResponse(Schedule schedule) {
            this.scheduleId = schedule.getId();
            this.location = schedule.getLocation();
            this.meetingTime = schedule.getMeetingTime();
            this.description = schedule.getDescription();
        }
    }

}