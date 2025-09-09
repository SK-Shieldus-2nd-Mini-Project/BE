// src/main/java/com/rookies4/MiniProject2/domain/entity/GroupMemberId.java
package com.rookies4.MiniProject2.domain.entity;

import java.io.Serializable;
import java.util.Objects;

// public으로 선언해야 다른 패키지에서 접근 가능합니다.
public class GroupMemberId implements Serializable {
    private Long user;
    private Long group;

    // hashCode and equals 구현
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