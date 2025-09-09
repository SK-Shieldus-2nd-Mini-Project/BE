package com.rookies4.MiniProject2.domain.entity;

import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Builder를 위해 추가
@Builder            // Builder 어노테이션 추가
public class GroupMember {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private JoinStatus status;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    protected void onApply() {
        this.appliedAt = LocalDateTime.now();
        // status가 null일 경우에만 PENDING으로 기본값 설정
        if (this.status == null) {
            this.status = JoinStatus.PENDING;
        }
    }
}