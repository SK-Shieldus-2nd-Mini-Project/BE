// src/main/java/com/rookies4/MiniProject2/domain/entity/Region.java
package com.rookies4.MiniProject2.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer id;

    // 필드명을 'name'에서 'regionName'으로 변경
    @Column(name = "region_name", nullable = false, unique = true, length = 50)
    private String regionName;

    // 생성자도 변경된 필드명으로 수정
    public Region(String regionName) {
        this.regionName = regionName;
    }
}