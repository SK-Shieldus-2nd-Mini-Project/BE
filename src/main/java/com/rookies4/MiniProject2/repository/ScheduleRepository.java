package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 필요한 쿼리 메서드가 있다면 여기에 추가합니다.
}