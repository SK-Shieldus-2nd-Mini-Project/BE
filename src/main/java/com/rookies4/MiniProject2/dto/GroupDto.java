package com.rookies4.MiniProject2.dto;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.GroupMember;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupDto {

    @Getter
    @NoArgsConstructor
    public static class ApplicationStatusResponse {
        private Long groupId;
        private String groupName;
        private JoinStatus status; // null if leader
        private LocalDateTime appliedAt;
        private boolean leader;

        @Builder
        public ApplicationStatusResponse(Long groupId, String groupName, JoinStatus status, LocalDateTime appliedAt, boolean leader) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.status = status;
            this.appliedAt = appliedAt;
            this.leader = leader;
        }
    }

    // 모임 생성 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "모임 이름은 필수입니다.")
        private String groupName;

        private String description;

        @NotNull(message = "지역 ID는 필수입니다.")
        private Integer regionId;

        @NotNull(message = "종목 ID는 필수입니다.")
        private Integer sportId;

        @NotNull(message = "최대 인원수는 필수입니다.")
        @Min(value = 2, message = "최대 인원수는 2명 이상이어야 합니다.")
        @Max(value = 100, message = "최대 인원수는 100명 이하이어야 합니다.")
        private int maxMembers;
        private MultipartFile image;
        private String imageUrl;
    }

    // 모임 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String groupName;
        private String description;
        private Integer regionId;
        private Integer sportId;
        private Integer maxMembers;
    }

    // 모임 생성 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class CreateResponse {
        private Long groupId;
        private String groupName;
        private ApprovalStatus approvalStatus;

        @Builder
        public CreateResponse(Group group) {
            this.groupId = group.getId();
            this.groupName = group.getGroupName();
            this.approvalStatus = group.getApprovalStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MyGroupResponse {
        private Long groupId;
        private String groupName;
        private String description;
        private String regionName;
        private String sportName;
        private boolean leader;
        private ApprovalStatus approvalStatus;
        private String imageUrl;

        @Builder
        public MyGroupResponse(Group group, User currentUser) {
            this.groupId = group.getId();
            this.groupName = group.getGroupName();
            this.description = group.getDescription();
            this.regionName = group.getRegion().getRegionName();
            this.sportName = group.getSport().getSportName();
            this.leader = group.getLeader().getId().equals(currentUser.getId());
            this.approvalStatus = group.getApprovalStatus();
            this.imageUrl = group.getImageUrl();
        }

        public MyGroupResponse(Group group) {
            this.groupId = group.getId();
            this.groupName = group.getGroupName();
            this.description = group.getDescription();
            this.regionName = group.getRegion().getRegionName();
            this.sportName = group.getSport().getSportName();
            this.leader = false; // 현재 사용자를 알 수 없으므로 기본값 false
            this.approvalStatus = group.getApprovalStatus();
            this.imageUrl = group.getImageUrl();
        }
    }

    // 모임 상세 조회 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class GroupDetailResponse {
        private Long groupId;
        private String groupName;
//        private String leaderNickname;
        private String regionName;
        private String sportName;
        private String description;
        private int maxMembers;
        private long currentMembers;
        private List<ScheduleDto.ScheduleResponse> schedules;
        private UserDto.MemberInfoResponse leaderInfo;
        private List<UserDto.MemberInfoResponse> members;
        private String imageUrl;

        @Builder
        public GroupDetailResponse(Group group, long currentMembers, List<UserDto.MemberInfoResponse> members) {
            this.groupId = group.getId();
            this.groupName = group.getGroupName();
//            this.leaderNickname = group.getLeader().getNickname();
            this.regionName = group.getRegion().getRegionName();
            this.sportName = group.getSport().getSportName();
            this.description = group.getDescription();
            this.maxMembers = group.getMaxMembers();
            this.currentMembers = currentMembers;
            this.leaderInfo = UserDto.MemberInfoResponse.builder().user(group.getLeader()).build(); // [수정]
            this.members = members;
            this.schedules = group.getSchedules().stream()
                    .map(schedule -> ScheduleDto.ScheduleResponse.builder().schedule(schedule).build())
                    .collect(Collectors.toList());
            this.imageUrl = group.getImageUrl();
        }
    }

}