package com.rookies4.MiniProject2.config;

import com.rookies4.MiniProject2.domain.entity.Region;
import com.rookies4.MiniProject2.domain.entity.Sport;
import com.rookies4.MiniProject2.repository.RegionRepository;
import com.rookies4.MiniProject2.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 지역 데이터가 이미 존재하는지 확인 후 초기화
        if (regionRepository.count() == 0) {
            System.out.println("지역 데이터 초기화 시작...");
            List<String> seoulDistricts = List.of(
                "강남구", "강동구", "강북구", "강서구", "관악구",
                "광진구", "구로구", "금천구", "노원구", "도봉구",
                "동대문구", "동작구", "마포구", "서대문구", "서초구",
                "성동구", "성북구", "송파구", "양천구", "영등포구",
                "용산구", "은평구", "종로구", "중구", "중랑구"
            );
            seoulDistricts.forEach(name -> {
                Region region = new Region();
                region.setName(name);
                regionRepository.save(region);
            });
            System.out.println("지역 데이터 초기화 완료.");
        }

        // 종목 데이터가 이미 존재하는지 확인 후 초기화
        if (sportRepository.count() == 0) {
            System.out.println("운동 종목 데이터 초기화 시작...");
            List<String> sports = List.of(
                "배드민턴", "축구", "농구", "런닝"
            );
            sports.forEach(name -> {
                Sport sport = new Sport();
                sport.setName(name);
                sportRepository.save(sport);
            });
            System.out.println("운동 종목 데이터 초기화 완료.");
        }
    }
}