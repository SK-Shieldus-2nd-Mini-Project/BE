package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.exception.BusinessLogicException;
import com.rookies4.MiniProject2.exception.EntityNotFoundException;
import com.rookies4.MiniProject2.exception.ErrorCode;
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
import org.springframework.util.StringUtils;

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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REGION_NOT_FOUND));

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPORT_NOT_FOUND));

        Group newGroup = Group.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .leader(leader)
                .region(region)
                .sport(sport)
                .maxMembers(request.getMaxMembers())
                .build();

        leader.getLeadingGroups().add(newGroup);

        groupRepository.save(newGroup);

        return GroupDto.CreateResponse.builder().group(newGroup).build();
    }

    // [관리자] 모임 생성 거절(삭제)
    @Transactional
    public void rejectGroup(Long groupId) {
        // ID로 그룹을 찾고, 없으면 예외 발생
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // 그룹 삭제
        groupRepository.delete(group);
    }

    public List<GroupDto.MyGroupResponse> findMyJoinedGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        return groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(groupMember -> new GroupDto.MyGroupResponse(groupMember.getGroup() ))
                .collect(Collectors.toList());
    }

    // 내가 만든 모임 + 내가 가입한 모임 목록 조회
    public List<GroupDto.MyGroupResponse> getMyAllGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        List<Group> joinedGroups = groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(GroupMember::getGroup)
                .toList();

        List<Group> leadingGroups = user.getLeadingGroups();

        // ✅ DTO 생성자에 user 객체를 넘겨주도록 수정
        return Stream.concat(joinedGroups.stream(), leadingGroups.stream())
                .distinct()
                .map(group -> new GroupDto.MyGroupResponse(group, user))
                .collect(Collectors.toList());
    }

    // [사용자] 나의 모임 목록 조회
    public List<GroupDto.MyGroupResponse> getMyGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        return groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)
                .stream()
                .map(groupMember -> new GroupDto.MyGroupResponse(groupMember.getGroup()))
                .collect(Collectors.toList());
    }

    // [사용자] 모임 상세 정보 조회
    public GroupDto.GroupDetailResponse getGroupDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // 이미 가입 신청했거나 멤버인지 확인
        groupMemberRepository.findByUserAndGroup(user, group).ifPresent(m -> {
            throw new BusinessLogicException(ErrorCode.ALREADY_JOINED_OR_PENDING);
        });

        // 팀장은 자신의 모임에 가입 신청할 수 없음 (이미 소속된 것으로 간주)
        if (group.getLeader().getId().equals(user.getId())) {
            throw new BusinessLogicException(ErrorCode.LEADER_CANNOT_JOIN);
        }

        // 최대 인원수 확인 로직, 최대인원수 넘어가면 신청 불가
        long currentMembers = groupMemberRepository.countByGroupAndStatus(group, JoinStatus.APPROVED);
        if(currentMembers >= group.getMaxMembers()){
            throw new BusinessLogicException(ErrorCode.MAX_MEMBERS_REACHED);
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 요청한 사용자가 실제 모임의 리더인지 확인
        if (!group.getLeader().getId().equals(leader.getId())) {
            throw new AccessDeniedException("모임의 리더만 가입을 승인할 수 있습니다.");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(applicant, group)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

        // 이미 처리된 신청인지 확인
        if (groupMember.getStatus() != JoinStatus.PENDING) {
            throw new BusinessLogicException(ErrorCode.ALREADY_JOINED_OR_PENDING);
        }

        // 상태를 'APPROVED'로 변경
        groupMember.setStatus(JoinStatus.APPROVED);
    }

    // [사용자:팀장] 가입 신청 거절 메서드 (추가)
    @Transactional
    public void rejectJoinRequest(Long groupId, Long applicantId, String leaderUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 요청한 사용자가 실제 모임의 리더인지 확인
        if (!group.getLeader().getId().equals(leader.getId())) {
            throw new AccessDeniedException("모임의 리더만 가입을 거절할 수 있습니다.");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(applicant, group)
                .orElseThrow(() -> new  BusinessLogicException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // 권한 확인: 모임의 리더이거나 관리자(ADMIN)만 수정 가능
        if (!group.getLeader().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("모임 정보를 수정할 권한이 없습니다.");
        }

        // groupName이 요청에 포함되었고, 비어있지 않은 경우에만 업데이트
        if (StringUtils.hasText(request.getGroupName())) {
            group.setGroupName(request.getGroupName());
        }

        // description이 요청에 포함된 경우에만 업데이트 (null 허용)
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        // regionId가 요청에 포함된 경우, 해당 Region을 찾아 업데이트
        if (request.getRegionId() != null) {
            Region region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REGION_NOT_FOUND));
            group.setRegion(region);
        }

        // sportId가 요청에 포함된 경우, 해당 Sport를 찾아 업데이트
        if (request.getSportId() != null) {
            Sport sport = sportRepository.findById(request.getSportId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPORT_NOT_FOUND));
            group.setSport(sport);
        }

        // maxMembers가 요청에 포함된 경우에만 업데이트
        if (request.getMaxMembers() != null) {
            // 최대 인원수 유효성 검사
            int maxMembers = request.getMaxMembers();
            if (maxMembers < 2 || maxMembers > 100) {
                throw new IllegalArgumentException("최대 인원수는 2명 이상, 100명 이하이어야 합니다.");
            }
            long currentMembers = groupMemberRepository.countByGroupAndStatus(group, JoinStatus.APPROVED);
            if (maxMembers < currentMembers) {
                throw new BusinessLogicException(ErrorCode.UPDATE_MAX_MEMBER_INVALID);
            }
            group.setMaxMembers(maxMembers);
        }

    }

    // 모임 삭제
    @Transactional
    public void deleteGroup(Long groupId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // 권한 확인: 모임의 리더이거나 관리자(ADMIN)만 삭제 가능
        if (!group.getLeader().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("모임을 삭제할 권한이 없습니다.");
        }

        groupRepository.delete(group);
    }

    // 가입 신청자 목록 조회
    public List<UserDto.ApplicantResponse> getApplicants(Long groupId, String leaderUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        User leader = userRepository.findByUsername(leaderUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // 모임의 리더는 탈퇴할 수 없음 (모임 삭제나 리더 위임 기능이 필요)
        if (group.getLeader().getId().equals(user.getId())) {
            throw new BusinessLogicException(ErrorCode.LEADER_CANNOT_LEAVE);
        }

        // 사용자의 가입 정보를 찾음
        GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NOT_A_MEMBER));

        // 가입 승인(APPROVED) 상태인 멤버만 탈퇴 가능
        if (groupMember.getStatus() != JoinStatus.APPROVED) {
            throw new BusinessLogicException(ErrorCode.NOT_APPROVED_MEMBER);
        }

        // 멤버 정보 삭제
        groupMemberRepository.delete(groupMember);
    }
}