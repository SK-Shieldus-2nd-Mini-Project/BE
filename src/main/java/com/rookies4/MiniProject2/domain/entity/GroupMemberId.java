package com.rookies4.MiniProject2.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class GroupMemberId implements Serializable {
    private Long user;
    private Long group;

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