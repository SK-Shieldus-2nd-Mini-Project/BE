package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.exception.BusinessLogicException;
import com.rookies4.MiniProject2.exception.EntityNotFoundException;
import com.rookies4.MiniProject2.exception.ErrorCode;
import com.rookies4.MiniProject2.repository.GroupRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j; // [추가] Slf4j import

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;

    // 내 정보 조회
    public UserDto.UserInfoResponse getMyInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        return UserDto.UserInfoResponse.builder().user(user).build();
    }

    //내 정보 수정 (닉네임 중복 검사 포함)
    @Transactional
    public void updateMyInfo(String username, UserDto.UpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 1. 닉네임 변경 요청이 있고, 기존 닉네임과 다른 경우에만 중복 검사
        if (StringUtils.hasText(request.getNickname()) && !user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new BusinessLogicException(ErrorCode.NICKNAME_DUPLICATION);
            }
            user.setNickname(request.getNickname());
        }

        // 2. 생년월일 변경 요청이 있는 경우
        if (request.getBirthdate() != null) {
            user.setBirthdate(request.getBirthdate());
        }

        // 3. 프로필 이미지 URL 변경 요청이 있는 경우
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // 4. 비밀번호 변경 로직
        if (StringUtils.hasText(request.getPassword())) {
            // 새 비밀번호를 암호화하여 설정
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String username) {
        // ==================== [수정] 페치 조인으로 User와 leadingGroups 함께 조회 ====================
        User user = userRepository.findByUsernameWithLeadingGroups(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 팀장으로 있는 모임이 있는지 확인하고, 있다면 모두 삭제 (모임 해산)
        List<Group> leadingGroups = user.getLeadingGroups();
        if (leadingGroups != null && !leadingGroups.isEmpty()) {
            // Group 엔티티의 cascade 설정에 따라 연관된 GroupMember, Schedule도 함께 삭제됨
            groupRepository.deleteAll(leadingGroups);
        }

        userRepository.delete(user);
    }

    // 관리자용 기능

    // 전체 회원 목록 조회
    public List<UserDto.UserInfoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDto.UserInfoResponse.builder().user(user).build())
                .collect(Collectors.toList());
    }

    // 관리자에 의한 회원 강제 탈퇴
    @Transactional
    public void deleteUserByAdmin(Long userId) {
        // ==================== [수정] 페치 조인으로 User와 leadingGroups 함께 조회 ====================
        User user = userRepository.findByIdWithLeadingGroups(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 기존 deleteUser 로직과 동일하게 팀장으로 있는 모임 처리
        List<Group> leadingGroups = user.getLeadingGroups();
        if (leadingGroups != null && !leadingGroups.isEmpty()) {
            groupRepository.deleteAll(leadingGroups);
        }
        userRepository.delete(user);
    }

    // 관리자용 회원 정보 수정 메서드
    @Transactional
    public void updateUserByAdmin(Long userId, UserDto.AdminUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 닉네임 변경 및 중복 검사
        if (StringUtils.hasText(request.getNickname()) && !user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new BusinessLogicException(ErrorCode.NICKNAME_DUPLICATION);
            }
            user.setNickname(request.getNickname());
        }

        // 생년월일 변경
        if (request.getBirthdate() != null) {
            user.setBirthdate(request.getBirthdate());
        }

        // 프로필 이미지 URL 변경
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // 비밀번호 변경 (암호화)
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 역할(Role) 변경
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
    }
}