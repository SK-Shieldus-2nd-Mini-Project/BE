package com.rookies4.MiniProject2;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.dto.UserDto;
import com.rookies4.MiniProject2.repository.GroupRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import com.rookies4.MiniProject2.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Test
    @DisplayName("회원 탈퇴 시 팀장으로 있는 모임은 함께 삭제(해산)된다")
    void deleteUser_WhenUserIsLeader_ShouldDeleteGroups() {
        // given
        User leader = User.builder().username("leaderUser").build();
        Group group1 = Group.builder().id(1L).leader(leader).build();
        Group group2 = Group.builder().id(2L).leader(leader).build();
        leader.setLeadingGroups(Arrays.asList(group1, group2));

        when(userRepository.findByUsername("leaderUser")).thenReturn(Optional.of(leader));

        // when
        userService.deleteUser("leaderUser");

        // then
        // groupRepository.deleteAll()이 leader의 모임 목록과 함께 호출되었는지 검증
        verify(groupRepository, times(1)).deleteAll(leader.getLeadingGroups());
        // userRepository.delete()가 leader 객체와 함께 호출되었는지 검증
        verify(userRepository, times(1)).delete(leader);
    }

    @Test
    @DisplayName("관리자가 전체 회원 목록을 조회한다")
    void getAllUsers_ShouldReturnAllUsers() {
        // given
        User user1 = User.builder().id(1L).username("user1").nickname("nick1").birthdate(LocalDate.now()).build();
        User user2 = User.builder().id(2L).username("user2").nickname("nick2").birthdate(LocalDate.now()).build();
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // when
        List<UserDto.UserInfoResponse> allUsers = userService.getAllUsers();

        // then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers.get(0).getUsername()).isEqualTo("user1");
        assertThat(allUsers.get(1).getNickname()).isEqualTo("nick2");
    }

    @Test
    @DisplayName("관리자가 특정 회원을 ID로 강제 탈퇴시킨다")
    void deleteUserByAdmin_ShouldDeleteUserAndGroups() {
        // given
        Long userId = 1L;
        User leader = User.builder().id(userId).username("leaderUser").build();
        Group group1 = Group.builder().id(1L).leader(leader).build();
        leader.setLeadingGroups(List.of(group1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(leader));

        // when
        userService.deleteUserByAdmin(userId);

        // then
        verify(groupRepository, times(1)).deleteAll(leader.getLeadingGroups());
        verify(userRepository, times(1)).delete(leader);
    }
}