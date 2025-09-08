package com.rookies4.MiniProject2.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // import 추가
import lombok.Builder;       // import 추가
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor
@AllArgsConstructor // Builder를 위해 추가
@Builder            // Builder 어노테이션 추가
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private String location;

    @Column(name = "meeting_time", nullable = false)
    private LocalDateTime meetingTime;

    @Lob
    private String description;
}