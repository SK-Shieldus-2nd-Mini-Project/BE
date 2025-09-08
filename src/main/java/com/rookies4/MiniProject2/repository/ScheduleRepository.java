package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}