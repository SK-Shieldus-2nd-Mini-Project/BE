package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.service.UserService;
import com.rookies4.MiniProject2.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GroupService groupService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDto.UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getUsername()));
    }

    // 내 정보 수정
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserDto.UpdateRequest request) {
        userService.updateMyInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 내 모든 모임 목록 조회 (가입한 모임 + 내가 만든 모임)
    @GetMapping("/me/groups")
    public ResponseEntity<List<GroupDto.MyGroupResponse>> getMyAllGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(groupService.getMyAllGroups(userDetails.getUsername()));
    }
}