package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.entity.GroupMember;
import com.rookies4.MiniProject2.domain.entity.GroupMemberId;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    // 특정 사용자가 속한 모임 목록 조회
    List<GroupMember> findByUserAndStatus(User user, JoinStatus status);
    // 특정 모임의 현재 인원수 조회
    long countByGroupAndStatus(Group group, JoinStatus status);
    // 특정 모임의 멤버/신청자 목록 조회
    List<GroupMember> findByGroupAndStatus(Group group, JoinStatus status);
    // User와 Group으로 GroupMember를 찾는 메서드
    Optional<GroupMember> findByUserAndGroup(User user, Group group);

    // ==================== [수정] N+1 문제 해결을 위한 페치 조인 적용 ====================
    @Query("SELECT gm FROM GroupMember gm " +
            "JOIN FETCH gm.group g " +
            "JOIN FETCH g.region " +
            "JOIN FETCH g.sport " +
            "WHERE gm.user = :user AND gm.status = :status")
    List<GroupMember> findByUserAndStatusWithGroup(@Param("user") User user, @Param("status") JoinStatus status);
    // ==============================================================================
}