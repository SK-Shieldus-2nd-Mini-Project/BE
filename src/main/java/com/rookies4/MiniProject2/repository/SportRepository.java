package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends JpaRepository<Sport, Integer> {
}
