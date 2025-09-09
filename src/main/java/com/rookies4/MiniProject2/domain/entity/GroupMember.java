package com.rookies4.MiniProject2.domain.entity;

import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberId.class)
@Getter
@NoArgsConstructor
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

    @Builder
    public GroupMember(User user, Group group, JoinStatus status, LocalDateTime appliedAt) {
        this.user = user;
        this.group = group;
        this.status = status;
        this.appliedAt = appliedAt;
    }
}