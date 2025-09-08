// src/main/java/com/rookies4/MiniProject2/repository/RegionRepository.java
package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Integer> {
}