package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // 관리자 승인 대기중인 모임 목록 조회
    List<Group> findByApprovalStatus(ApprovalStatus approvalStatus);
}