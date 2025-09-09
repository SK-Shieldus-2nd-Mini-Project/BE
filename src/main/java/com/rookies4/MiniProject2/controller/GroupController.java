package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<String> createGroup(@Valid @RequestBody GroupDto.CreateRequest request) {
        groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임이 성공적으로 생성되었습니다.");
    }

    @GetMapping
    public ResponseEntity<List<GroupDto.GroupListResponse>> getGroups(
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) Integer sportId) {
        List<GroupDto.GroupListResponse> groups = groupService.getGroups(regionId, sportId);
        return ResponseEntity.ok(groups);
    }
}