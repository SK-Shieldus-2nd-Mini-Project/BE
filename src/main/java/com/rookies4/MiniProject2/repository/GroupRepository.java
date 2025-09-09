package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Group;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<Group> findByRegionIdAndSportIdAndApprovalStatus(Integer regionId, Integer sportId, ApprovalStatus approvalStatus);
    List<Group> findByRegionIdAndApprovalStatus(Integer regionId, ApprovalStatus approvalStatus);
    List<Group> findBySportIdAndApprovalStatus(Integer sportId, ApprovalStatus approvalStatus);
}