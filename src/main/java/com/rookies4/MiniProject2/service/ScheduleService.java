package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.Schedule;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.ScheduleDto;
import com.rookies4.MiniProject2.exception.BusinessLogicException;
import com.rookies4.MiniProject2.exception.EntityNotFoundException;
import com.rookies4.MiniProject2.exception.ErrorCode;
import com.rookies4.MiniProject2.repository.GroupRepository;
import com.rookies4.MiniProject2.repository.ScheduleRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleDto.ScheduleResponse createSchedule(Long groupId, ScheduleDto.CreateRequest request, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 현재 사용자가 해당 모임의 팀장인지 확인
        if (!group.getLeader().getId().equals(user.getId())) {
            throw new AccessDeniedException("일정을 생성할 권한이 없습니다. (팀장만 가능)");
        }

        Schedule newSchedule = Schedule.builder()
                .group(group)
                .location(request.getLocation())
                .meetingTime(request.getMeetingTime())
                .description(request.getDescription())
                .build();

        scheduleRepository.save(newSchedule);

        return ScheduleDto.ScheduleResponse.builder().schedule(newSchedule).build();
    }

    // 특정 모임의 전체 일정 목록 조회
    @Transactional(readOnly = true)
    public List<ScheduleDto.ScheduleResponse> getSchedules(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.GROUP_NOT_FOUND));

        // Group 엔티티가 이미 Schedule 목록을 가지고 있으므로, 이를 DTO로 변환하여 반환
        return group.getSchedules().stream()
                .map(schedule -> ScheduleDto.ScheduleResponse.builder().schedule(schedule).build())
                .collect(Collectors.toList());
    }

    // 일정 수정
    @Transactional
    public void updateSchedule(Long groupId, Long scheduleId, ScheduleDto.UpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 요청한 모임(groupId)의 일정이 맞는지, 그리고 요청자가 팀장인지 확인
        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new BusinessLogicException(ErrorCode.INVALID_SCHEDULE_FOR_GROUP);
        }
        if (!schedule.getGroup().getLeader().getId().equals(user.getId())) {
            throw new AccessDeniedException("일정을 수정할 권한이 없습니다. (팀장만 가능)");
        }

        schedule.setLocation(request.getLocation());
        schedule.setMeetingTime(request.getMeetingTime());
        schedule.setDescription(request.getDescription());
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long groupId, Long scheduleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 요청한 모임(groupId)의 일정이 맞는지, 그리고 요청자가 팀장인지 확인
        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new BusinessLogicException(ErrorCode.INVALID_SCHEDULE_FOR_GROUP);
        }
        if (!schedule.getGroup().getLeader().getId().equals(user.getId())) {
            throw new AccessDeniedException("일정을 삭제할 권한이 없습니다. (팀장만 가능)");
        }

        scheduleRepository.delete(schedule);
    }
}