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
}