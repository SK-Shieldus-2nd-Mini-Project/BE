package com.rookies4.MiniProject2.controller;

import com.rookies4.MiniProject2.dto.ScheduleDto;
import com.rookies4.MiniProject2.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleDto.ScheduleResponse> createSchedule(
            @PathVariable Long groupId,
            @Valid @RequestBody ScheduleDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ScheduleDto.ScheduleResponse response = scheduleService.createSchedule(groupId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== [추가] 일정 목록 조회 API ====================
    @GetMapping
    public ResponseEntity<List<ScheduleDto.ScheduleResponse>> getSchedules(@PathVariable Long groupId) {
        return ResponseEntity.ok(scheduleService.getSchedules(groupId));
    }

    // ==================== [추가] 일정 수정 API ====================
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        scheduleService.updateSchedule(groupId, scheduleId, request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // ==================== [추가] 일정 삭제 API ====================
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserDetails userDetails) {

        scheduleService.deleteSchedule(groupId, scheduleId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}