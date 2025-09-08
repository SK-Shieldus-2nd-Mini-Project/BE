package com.rookies4.MiniProject2.runner;

import com.rookies4.MiniProject2.domain.entity.Region;
import com.rookies4.MiniProject2.domain.entity.Sport;
import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.repository.RegionRepository;
import com.rookies4.MiniProject2.repository.SportRepository;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. 지역 및 스포츠 마스터 데이터 생성 (ID 값 제거)
        regionRepository.saveAll(Arrays.asList(
                new Region("서울 강남구"),
                new Region("서울 중구")
        ));
        sportRepository.saveAll(Arrays.asList(
                new Sport("러닝"),
                new Sport("자전거"),
                new Sport("풋살")
        ));

        // 2. 테스트용 사용자(관리자, 일반사용자) 생성 (이 부분은 변경 없음)
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123!"))
                .nickname("관리자")
                .birthdate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .build();

        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123!"))
                .nickname("일반사용자")
                .birthdate(LocalDate.of(1995, 5, 5))
                .role(Role.USER)
                .build();

        userRepository.saveAll(Arrays.asList(admin, user));
    }
}
// Region, Sport, User 엔티티에 Builder 또는 생성자 추가 필요