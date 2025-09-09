package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.dto.GroupDto;
import com.rookies4.MiniProject2.repository.GroupMemberRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    // 테스트용 가짜(mock) Region과 Sport 객체를 미리 생성
    private Region mockRegion;
    private Sport mockSport;

    @BeforeEach
    void setUp() {
        // 각 테스트가 실행되기 전에 mock 객체들을 초기화
        mockRegion = new Region(1, "테스트지역");
        mockSport = new Sport(1, "테스트종목");
    }

    @Test
    @DisplayName("내 모든 모임 조회 시 내가 만든 모임과 가입한 모임이 모두 조회된다")
    void getMyAllGroups_ShouldReturnLeadingAndJoinedGroups() {
        // given
        String username = "testUser";
        User user = User.builder().username(username).leadingGroups(new ArrayList<>()).build();

        Group leadingGroup = Group.builder().id(1L).groupName("내가 만든 모임")
                .region(mockRegion) // mock Region 설정
                .sport(mockSport)   // mock Sport 설정
                .build();
        user.getLeadingGroups().add(leadingGroup);

        Group joinedGroup = Group.builder().id(2L).groupName("내가 가입한 모임")
                .region(mockRegion) // mock Region 설정
                .sport(mockSport)   // mock Sport 설정
                .build();
        GroupMember groupMember = GroupMember.builder().user(user).group(joinedGroup).status(JoinStatus.APPROVED).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)).thenReturn(List.of(groupMember));

        // when
        List<GroupDto.MyGroupResponse> myAllGroups = groupService.getMyAllGroups(username);

        // then
        assertThat(myAllGroups).hasSize(2);
        assertThat(myAllGroups).extracting("groupName").containsExactlyInAnyOrder("내가 만든 모임", "내가 가입한 모임");
    }

    @Test
    @DisplayName("내가 팀장이면서 멤버인 모임은 중복 없이 한 번만 조회된다")
    void getMyAllGroups_WhenLeaderAndMember_ShouldReturnDistinct() {
        // given
        String username = "testUser";
        User user = User.builder().username(username).leadingGroups(new ArrayList<>()).build();

        Group group = Group.builder().id(1L).groupName("리더이자 멤버인 모임")
                .region(mockRegion) // mock Region 설정
                .sport(mockSport)   // mock Sport 설정
                .build();
        user.getLeadingGroups().add(group);
        GroupMember groupMember = GroupMember.builder().user(user).group(group).status(JoinStatus.APPROVED).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(groupMemberRepository.findByUserAndStatus(user, JoinStatus.APPROVED)).thenReturn(List.of(groupMember));

        // when
        List<GroupDto.MyGroupResponse> myAllGroups = groupService.getMyAllGroups(username);

        // then
        assertThat(myAllGroups).hasSize(1);
        assertThat(myAllGroups.get(0).getGroupName()).isEqualTo("리더이자 멤버인 모임");
    }
}