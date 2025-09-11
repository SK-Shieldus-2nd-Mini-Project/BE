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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 사용자 데이터 초기화 (기존과 동일)
        if (userRepository.count() == 0) {
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

        // 지역 데이터 초기화 (요청하신 데이터로 변경)
        if (regionRepository.count() == 0) {
            List<String> regionNames = Arrays.asList(
                    "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
                    "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
                    "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
            );
            List<Region> regions = regionNames.stream().map(Region::new).collect(Collectors.toList());
            regionRepository.saveAll(regions);
        }

        // 운동 종목 데이터 초기화 (요청하신 데이터로 변경)
        if (sportRepository.count() == 0) {
            List<String> sportNames = Arrays.asList(
                    "농구", "등산", "러닝", "배드민턴", "볼링", "야구", "자전거", "족구", "축구", "탁구"
            );
            List<Sport> sports = sportNames.stream().map(Sport::new).collect(Collectors.toList());
            sportRepository.saveAll(sports);
        }
    }
}