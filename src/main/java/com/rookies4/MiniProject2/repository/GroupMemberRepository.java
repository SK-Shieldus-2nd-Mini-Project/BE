package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.GroupMember;
import com.rookies4.MiniProject2.domain.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    // 필요한 쿼리 메서드가 있다면 여기에 추가합니다.
}