package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);

    // ==================== N+1 문제 해결을 위한 페치 조인 적용 ====================
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.leadingGroups WHERE u.username = :username")
    Optional<User> findByUsernameWithLeadingGroups(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.leadingGroups WHERE u.id = :userId")
    Optional<User> findByIdWithLeadingGroups(@Param("userId") Long userId);
    // ==============================================================================
}