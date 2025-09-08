package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.repository.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository; // 추가
    private final SportRepository sportRepository;   // 추가

    // [사용자] 모임 생성 메서드 (추가)
    @Transactional
    public GroupDto.CreateResponse createGroup(GroupDto.CreateRequest request, String username) {
        User leader = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

        Group newGroup = Group.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .leader(leader)
                .region(region)
                .sport(sport)
                .maxMembers(request.getMaxMembers())
                .build();

        groupRepository.save(newGroup);

        return GroupDto.CreateResponse.builder().group(newGroup).build();
    }

    // [관리자] 모임 생성 거절(삭제)
    @Transactional
    public void rejectGroup(Long groupId) {
        // ID로 그룹을 찾고, 없으면 예외 발생
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + groupId + "에 해당하는 모임을 찾을 수 없습니다."));

        // 그룹 삭제
        groupRepository.delete(group);
    }

    // [사용자] 나의 모임 목록 조회
    public List<GroupDto.MyGroupResponse> getMyGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(groupMember -> new GroupDto.MyGroupResponse(groupMember.getGroup()))
                .collect(Collectors.toList());
    }

    // [사용자] 모임 상세 정보 조회
    public GroupDto.GroupDetailResponse getGroupDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));
        long currentMembers = groupMemberRepository.countByGroupAndStatus(group, JoinStatus.APPROVED);
        return GroupDto.GroupDetailResponse.builder()
                .group(group)
                .currentMembers(currentMembers)
                .build();
    }

    // [사용자] 모임 가입 신청 메서드 (추가)
    @Transactional
    public void joinGroup(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        // 이미 가입 신청했거나 멤버인지 확인
        groupMemberRepository.findByUserAndGroup(user, group).ifPresent(m -> {
            throw new IllegalStateException("이미 가입 신청했거나 가입된 모임입니다.");
        });

        // 팀장은 자신의 모임에 가입 신청할 수 없음 (이미 소속된 것으로 간주)
        if (group.getLeader().getId().equals(user.getId())) {
            throw new IllegalStateException("모임의 리더는 가입 신청할 수 없습니다.");
        }

        GroupMember newMember = GroupMember.builder()
                .user(user)
                .group(group)
                .build();

        groupMemberRepository.save(newMember);
    }

    // [사용자:팀장] 가입 신청 승인 메서드 (추가)
    @Transactional
    public void approveJoinRequest(Long groupId, Long applicantId, String leaderUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("리더 정보를 찾을 수 없습니다."));

        // 요청한 사용자가 실제 모임의 리더인지 확인
        if (!group.getLeader().getId().equals(leader.getId())) {
            throw new AccessDeniedException("모임의 리더만 가입을 승인할 수 있습니다.");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new UsernameNotFoundException("신청자 정보를 찾을 수 없습니다."));

        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(applicant, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 가입 신청 정보를 찾을 수 없습니다."));

        // 이미 처리된 신청인지 확인
        if (groupMember.getStatus() != JoinStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        // 상태를 'APPROVED'로 변경
        groupMember.setStatus(JoinStatus.APPROVED);
    }

    // [사용자:팀장] 가입 신청 거절 메서드 (추가)
    @Transactional
    public void rejectJoinRequest(Long groupId, Long applicantId, String leaderUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("리더 정보를 찾을 수 없습니다."));

        // 요청한 사용자가 실제 모임의 리더인지 확인
        if (!group.getLeader().getId().equals(leader.getId())) {
            throw new AccessDeniedException("모임의 리더만 가입을 거절할 수 있습니다.");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new UsernameNotFoundException("신청자 정보를 찾을 수 없습니다."));

        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(applicant, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 가입 신청 정보를 찾을 수 없습니다."));

        // 가입 신청 기록 삭제
        groupMemberRepository.delete(groupMember);
    }

    // [관리자] 모집 승인 대기 목록 조회
    public List<GroupDto.MyGroupResponse> getPendingGroups() {
        return groupRepository.findByApprovalStatus(ApprovalStatus.PENDING)
                .stream()
                .map(GroupDto.MyGroupResponse::new)
                .collect(Collectors.toList());
    }

    // [관리자] 모임 생성 승인
    @Transactional
    public void approveGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));
        group.setApprovalStatus(ApprovalStatus.APPROVED);
        groupRepository.save(group);
    }
}