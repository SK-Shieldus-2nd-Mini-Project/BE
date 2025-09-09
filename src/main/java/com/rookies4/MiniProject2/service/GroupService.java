package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Group createGroup(GroupDto.CreateRequest request) {
        // 1. JWT 토큰에서 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User leader = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 요청 데이터의 Region, Sport 엔티티 찾기
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));
        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

        // 3. 새로운 Group 엔티티 생성 및 저장
        Group newGroup = Group.builder()
                .leader(leader)
                .region(region)
                .sport(sport)
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .maxMembers(request.getMaxMembers())
                .approvalStatus(ApprovalStatus.PENDING) // 관리자 승인 대기 상태로 생성
                .createdAt(LocalDateTime.now())
                .build();

        Group savedGroup = groupRepository.save(newGroup);

        // 4. 모임장(리더)를 모임 구성원으로 추가
        GroupMember leaderMember = GroupMember.builder()
                .user(leader)
                .group(savedGroup)
                .status(JoinStatus.APPROVED) // 모임장은 가입 상태를 '승인'으로 설정
                .appliedAt(LocalDateTime.now())
                .build();
        groupMemberRepository.save(leaderMember);

        
        Schedule schedule = Schedule.builder()
                .group(savedGroup)
                .location(request.getLocation())
                .meetingTime(request.getMeetingTime())
                .description("첫 번째 모임 일정입니다.")
                .build();
        scheduleRepository.save(schedule);

        return savedGroup;
    }

    @Transactional
    public List<GroupDto.GroupListResponse> getGroups(Integer regionId, Integer sportId) {
        List<Group> groups;
        if (regionId != null && sportId != null) {
            groups = groupRepository.findByRegionIdAndSportIdAndApprovalStatus(regionId, sportId, ApprovalStatus.APPROVED);
        } else if (regionId != null) {
            groups = groupRepository.findByRegionIdAndApprovalStatus(regionId, ApprovalStatus.APPROVED);
        } else if (sportId != null) {
            groups = groupRepository.findBySportIdAndApprovalStatus(sportId, ApprovalStatus.APPROVED);
        } else {
            groups = groupRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
        }

        return groups.stream().map(group ->
                GroupDto.GroupListResponse.builder()
                        .groupId(group.getId())
                        .groupName(group.getGroupName())
                        .regionName(group.getRegion().getName())
                        .sportName(group.getSport().getName())
                        .leaderNickname(group.getLeader().getNickname())
                        .currentMembers(group.getMembers().size())
                        .maxMembers(group.getMaxMembers())
                        .createdAt(group.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }
}