package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    public UserDto.UserInfoResponse getMyInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return UserDto.UserInfoResponse.builder().user(user).build();
    }

    // 내 정보 수정
    /**
     * 내 정보 수정 (닉네임 중복 검사 포함)
     */
    @Transactional
    public void updateMyInfo(String username, UserDto.UpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1. 닉네임 변경 요청이 있고, 기존 닉네임과 다른 경우에만 중복 검사
        if (StringUtils.hasText(request.getNickname()) && !user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
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

        // ======== 4. 비밀번호 변경 로직 (추가) ========
        if (StringUtils.hasText(request.getPassword())) {
            // 새 비밀번호를 암호화하여 설정
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        // 회원 탈퇴 시 팀장으로 있는 모임 처리 로직 필요 (위임 또는 해체)
        userRepository.delete(user);
    }

    // ======== 관리자용 회원 정보 수정 메서드 (추가) ========
    @Transactional
    public void updateUserByAdmin(Long userId, UserDto.AdminUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 닉네임 변경 및 중복 검사
        if (StringUtils.hasText(request.getNickname()) && !user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
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