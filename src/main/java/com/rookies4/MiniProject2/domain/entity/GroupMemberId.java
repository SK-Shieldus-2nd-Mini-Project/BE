package com.rookies4.MiniProject2.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
public class GroupMemberId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(user, that.user) && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, group);
    }
}
