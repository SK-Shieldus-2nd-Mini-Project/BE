// src/main/java/com/rookies4/MiniProject2/domain/entity/Sport.java
package com.rookies4.MiniProject2.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id")
    private Integer id;

    // 필드명을 'name'에서 'sportName'으로 변경
    @Column(name = "sport_name", nullable = false, unique = true, length = 50)
    private String sportName;

    // 생성자도 변경된 필드명으로 수정
    public Sport(String sportName) {
        this.sportName = sportName;
    }
}