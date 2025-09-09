// src/main/java/com/rookies4/MiniProject2/repository/SportRepository.java
package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Integer> {
}