// Sport.java
package com.rookies4.MiniProject2.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sports")
@Getter
@Setter
@NoArgsConstructor
public class Sport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id")
    private Integer id;

    @Column(name = "sport_name", nullable = false, unique = true, length = 50)
    private String name;
}