package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;


    // ======== 모임 생성 API (추가) ========
    @PostMapping
    public ResponseEntity<GroupDto.CreateResponse> createGroup(
            @Valid @RequestBody GroupDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        GroupDto.CreateResponse response = groupService.createGroup(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 나의 모임 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<GroupDto.MyGroupResponse>> getMyGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(groupService.getMyGroups(userDetails.getUsername()));
    }

    // 모임 상세 조회
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto.GroupDetailResponse> getGroupDetails(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetails(groupId));
    }

    // ======== 모임 모임 가입 신청 API (추가) ========
    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        groupService.joinGroup(groupId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
    // ======== 모임 가입 신청 승인 API (추가) ========
    @PostMapping("/{groupId}/applicants/{userId}/approve")
    public ResponseEntity<Void> approveJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        groupService.approveJoinRequest(groupId, userId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // ======== 모임 가입 신청 거절 API (추가) ========
    @PostMapping("/{groupId}/applicants/{userId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.rejectJoinRequest(groupId, userId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}