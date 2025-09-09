package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;

    // [사용자] 모임 생성 메서드
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

    public List<GroupDto.MyGroupResponse> findMyJoinedGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(groupMember -> new GroupDto.MyGroupResponse(groupMember.getGroup()))
                .collect(Collectors.toList());
    }

    // 내가 만든 모임 + 내가 가입한 모임 목록 조회
    public List<GroupDto.MyGroupResponse> getMyAllGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1. 내가 가입한 모임 목록 조회
        List<Group> joinedGroups = groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(GroupMember::getGroup)
                .toList();

        // 2. 내가 팀장인 모임 목록 조회 (User 엔티티에서 직접 가져옴)
        List<Group> leadingGroups = user.getLeadingGroups();

        // 3. 두 리스트를 합치고 중복을 제거한 후 DTO로 변환하여 반환
        return Stream.concat(joinedGroups.stream(), leadingGroups.stream())
                .distinct() // 중복된 모임 제거 (팀장이 멤버로도 등록되는 경우를 대비)
                .map(GroupDto.MyGroupResponse::new)
                .collect(Collectors.toList());
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

    // 모임 목록 전체 조회 (필터링 포함)
    public List<GroupDto.MyGroupResponse> getAllApprovedGroups(Integer regionId, Integer sportId) {
        // Specification을 사용하여 동적 쿼리 생성
        Specification<Group> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 기본 조건: 승인된(APPROVED) 모임만 조회
            predicates.add(cb.equal(root.get("approvalStatus"), ApprovalStatus.APPROVED));

            // 2. 지역 ID 필터링 조건 (파라미터가 있는 경우)
            if (regionId != null) {
                predicates.add(cb.equal(root.get("region").get("id"), regionId));
            }

            // 3. 종목 ID 필터링 조건 (파라미터가 있는 경우)
            if (sportId != null) {
                predicates.add(cb.equal(root.get("sport").get("id"), sportId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return groupRepository.findAll(spec).stream()
                .map(GroupDto.MyGroupResponse::new)
                .collect(Collectors.toList());
    }

    // 모임 정보 수정
    @Transactional
    public void updateGroup(Long groupId, GroupDto.UpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        // 권한 확인: 모임의 리더이거나 관리자(ADMIN)만 수정 가능
        if (!group.getLeader().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("모임 정보를 수정할 권한이 없습니다.");
        }

        // 현재 멤버 수보다 작게 최대 인원수를 변경할 수 없도록 방어
        long currentMembers = groupMemberRepository.countByGroupAndStatus(group, JoinStatus.APPROVED);
        if (request.getMaxMembers() < currentMembers) {
            throw new IllegalArgumentException("최대 인원수는 현재 인원수(" + currentMembers + "명)보다 적게 설정할 수 없습니다.");
        }

        // 정보 업데이트
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
    }

    // 모임 삭제
    @Transactional
    public void deleteGroup(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        // 권한 확인: 모임의 리더이거나 관리자(ADMIN)만 삭제 가능
        if (!group.getLeader().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("모임을 삭제할 권한이 없습니다.");
        }

        groupRepository.delete(group);
    }

    // 가입 신청자 목록 조회
    public List<UserDto.ApplicantResponse> getApplicants(Long groupId, String leaderUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("리더 정보를 찾을 수 없습니다."));

        // 요청한 사용자가 실제 모임의 리더인지 확인
        if (!group.getLeader().getId().equals(leader.getId())) {
            throw new AccessDeniedException("모임의 리더만 신청자 목록을 조회할 수 있습니다.");
        }

        // 'PENDING' 상태인 멤버 목록을 조회하여 DTO로 변환 후 반환
        return groupMemberRepository.findByGroupAndStatus(group, JoinStatus.PENDING)
                .stream()
                .map(groupMember -> UserDto.ApplicantResponse.builder().user(groupMember.getUser()).build())
                .collect(Collectors.toList());
    }

    // 모임 탈퇴
    @Transactional
    public void leaveGroup(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        // 모임의 리더는 탈퇴할 수 없음 (모임 삭제나 리더 위임 기능이 필요)
        if (group.getLeader().getId().equals(user.getId())) {
            throw new IllegalStateException("모임의 리더는 탈퇴할 수 없습니다. 모임을 삭제하거나 리더를 위임해주세요.");
        }

        // 사용자의 가입 정보를 찾음
        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));

        // 가입 승인(APPROVED) 상태인 멤버만 탈퇴 가능
        if (groupMember.getStatus() != JoinStatus.APPROVED) {
            throw new IllegalStateException("가입 승인된 멤버만 탈퇴가 가능합니다.");
        }

        // 멤버 정보 삭제
        groupMemberRepository.delete(groupMember);
    }
}