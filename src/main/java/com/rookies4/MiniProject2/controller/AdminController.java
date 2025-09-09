package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.rookies4.MiniProject2.service.UserService; // UserService import 추가


import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final GroupService groupService;
    private final UserService userService;

    // ==================== 모임 관리 ====================
    // 승인 대기 모임 목록 조회
    @GetMapping("/groups/pending")
    public ResponseEntity<List<GroupDto.MyGroupResponse>> getPendingGroups() {
        return ResponseEntity.ok(groupService.getPendingGroups());
    }

    // 모임 생성 승인
    @PostMapping("/groups/{groupId}/approve")
    public ResponseEntity<Void> approveGroup(@PathVariable Long groupId) {
        groupService.approveGroup(groupId);
        return ResponseEntity.ok().build();
    }

    // 모임 생성 거절 API
    @PostMapping("/groups/{groupId}/reject")
    public ResponseEntity<Void> rejectGroup(@PathVariable Long groupId) {
        groupService.rejectGroup(groupId);
        // 성공적으로 삭제되었으므로 본문(body) 없이 204 No Content 응답을 보냅니다.
        return ResponseEntity.noContent().build();
    }

    //  전체 모임 목록 조회
    @GetMapping("/groups")
    public ResponseEntity<List<GroupDto.MyGroupResponse>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllApprovedGroups(null, null));
    }


    // ==================== 회원 관리  ====================

    // 전체 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserDto.UserInfoResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 관리자용 회원 정보 수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> updateUserByAdmin(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.AdminUpdateRequest request) {

        userService.updateUserByAdmin(userId, request);
        return ResponseEntity.ok().build();
    }

    // 관리자용 회원 강제 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable Long userId) {
        userService.deleteUserByAdmin(userId);
        return ResponseEntity.noContent().build();
    }
}
