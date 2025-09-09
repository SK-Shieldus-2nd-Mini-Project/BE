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

    @Column(name = "sport_name", nullable = false, unique = true, length = 50)
    private String sportName;

    public Sport(String sportName) {
        this.sportName = sportName;
    }
}