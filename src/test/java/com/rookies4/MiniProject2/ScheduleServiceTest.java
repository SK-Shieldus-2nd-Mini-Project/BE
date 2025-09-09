package com.rookies4.MiniProject2;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.Schedule;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.ScheduleDto;
import com.rookies4.MiniProject2.repository.GroupRepository;
import com.rookies4.MiniProject2.repository.ScheduleRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import com.rookies4.MiniProject2.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("팀장이 일정을 생성하면 성공한다")
    void createSchedule_Success_WhenUserIsLeader() {
        // given
        User leader = User.builder().id(1L).username("leader").build();
        Group group = Group.builder().id(1L).leader(leader).build();
        ScheduleDto.CreateRequest request = new ScheduleDto.CreateRequest();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername("leader")).thenReturn(Optional.of(leader));

        // when
        scheduleService.createSchedule(1L, request, "leader");

        // then
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("팀장이 아닌 멤버가 일정 생성을 시도하면 AccessDeniedException 예외가 발생한다")
    void createSchedule_Fail_WhenUserIsNotLeader() {
        // given
        User leader = User.builder().id(1L).username("leader").build();
        User member = User.builder().id(2L).username("member").build();
        Group group = Group.builder().id(1L).leader(leader).build();
        ScheduleDto.CreateRequest request = new ScheduleDto.CreateRequest();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));

        // when & then
        assertThrows(AccessDeniedException.class, () -> {
            scheduleService.createSchedule(1L, request, "member");
        });
    }
}
