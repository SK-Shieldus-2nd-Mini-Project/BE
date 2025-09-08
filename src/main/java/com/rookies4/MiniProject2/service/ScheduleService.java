package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.Schedule;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.ScheduleDto;
import com.rookies4.MiniProject2.repository.GroupRepository;
import com.rookies4.MiniProject2.repository.ScheduleRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleDto.ScheduleResponse createSchedule(Long groupId, ScheduleDto.CreateRequest request, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

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
}