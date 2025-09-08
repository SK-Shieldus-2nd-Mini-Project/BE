package com.rookies4.MiniProject2.repository;

import com.rookies4.MiniProject2.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}

// GroupRepository, ScheduleRepository 등 다른 리포지토리도 유사하게 생성합니다.
// 예: public interface GroupRepository extends JpaRepository<Group, Long> {}
// 예: public interface SportRepository extends JpaRepository<Sport, Integer> {}
// 예: public interface RegionRepository extends JpaRepository<Region, Integer> {}