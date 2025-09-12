package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// JpaSpecificationExecutor<Group> 인터페이스 상속 추가
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    // 관리자 승인 대기중인 모임 목록 조회
    List<Group> findByApprovalStatus(ApprovalStatus approvalStatus);

    // ==================== [수정] N+1 문제 해결을 위한 페치 조인 적용 ====================
    @Query("SELECT g FROM Group g JOIN FETCH g.leader JOIN FETCH g.region JOIN FETCH g.sport WHERE g.id = :groupId")
    Optional<Group> findByIdWithDetails(@Param("groupId") Long groupId);
    // ==============================================================================
}