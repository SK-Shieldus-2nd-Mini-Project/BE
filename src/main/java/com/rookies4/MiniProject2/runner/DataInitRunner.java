//// sk-shieldus-2nd-mini-project/be/BE-mypage_mygroups/src/main/java/com/rookies4/MiniProject2/runner/DataInitRunner.java
//package com.rookies4.MiniProject2.runner;
//
//import com.rookies4.MiniProject2.domain.entity.Region;
//import com.rookies4.MiniProject2.domain.entity.Sport;
//import com.rookies4.MiniProject2.domain.entity.User;
//import com.rookies4.MiniProject2.domain.enums.Role;
//import com.rookies4.MiniProject2.repository.RegionRepository;
//import com.rookies4.MiniProject2.repository.SportRepository;
//import com.rookies4.MiniProject2.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitRunner implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final RegionRepository regionRepository;
//    private final SportRepository sportRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        // 사용자 데이터 초기화 (기존과 동일)
//        if (userRepository.count() == 0) {
//            User admin = User.builder()
//                    .username("admin")
//                    .password(passwordEncoder.encode("admin123!"))
//                    .nickname("관리자")
//                    .birthdate(LocalDate.of(1990, 1, 1))
//                    .role(Role.ADMIN)
//                    .build();
//
//            User user = User.builder()
//                    .username("user")
//                    .password(passwordEncoder.encode("user123!"))
//                    .nickname("일반사용자")
//                    .birthdate(LocalDate.of(1995, 5, 5))
//                    .role(Role.USER)
//                    .build();
//
//            userRepository.saveAll(Arrays.asList(admin, user));
//        }
//
//        // 지역 데이터 초기화 (요청하신 데이터로 변경)
//        if (regionRepository.count() == 0) {
//            List<String> regionNames = Arrays.asList(
//                    "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
//                    "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
//                    "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
//            );
//            List<Region> regions = regionNames.stream().map(Region::new).collect(Collectors.toList());
//            regionRepository.saveAll(regions);
//        }
//
//        // 운동 종목 데이터 초기화 (요청하신 데이터로 변경)
//        if (sportRepository.count() == 0) {
//            List<String> sportNames = Arrays.asList(
//                    "농구", "등산", "러닝", "배드민턴", "볼링", "야구", "자전거", "족구", "축구", "탁구"
//            );
//            List<Sport> sports = sportNames.stream().map(Sport::new).collect(Collectors.toList());
//            sportRepository.saveAll(sports);
//        }
//    }
//}


// sk-shieldus-2nd-mini-project/be/BE-mypage_mygroups/src/main/java/com/rookies4/MiniProject2/runner/DataInitRunner.java
package com.rookies4.MiniProject2.runner;

import com.rookies4.MiniProject2.domain.entity.*;
import com.rookies4.MiniProject2.domain.enums.ApprovalStatus;
import com.rookies4.MiniProject2.domain.enums.JoinStatus;
import com.rookies4.MiniProject2.domain.enums.Role;
import com.rookies4.MiniProject2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final SportRepository sportRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // DB에 데이터가 충분히 있으면 실행하지 않음
        if (userRepository.count() > 4) {
            return;
        }

        // ==================== [1] 메타데이터 생성 ====================
        // 지역 데이터 (서울시 전체)
        List<Region> regions = regionRepository.saveAll(
                Arrays.asList(
                        "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
                        "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
                        "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
                ).stream().map(Region::new).collect(Collectors.toList())
        );

        // 운동 종목 데이터 (한글 순)
        List<Sport> sports = sportRepository.saveAll(
                Arrays.asList(
                        "농구", "등산", "러닝", "배드민턴", "볼링", "야구", "자전거", "족구", "축구", "탁구"
                ).stream().map(Sport::new).collect(Collectors.toList())
        );


        // ==================== [2] 사용자 데이터 생성 ====================
        User admin = userRepository.save(User.builder().username("admin").password(passwordEncoder.encode("admin123!")).nickname("관리자").birthdate(LocalDate.of(1990, 1, 1)).role(Role.ADMIN).build());
        User leader1 = userRepository.save(User.builder().username("leader1").password(passwordEncoder.encode("leader123!")).nickname("달리기모임장").birthdate(LocalDate.of(1992, 3, 10)).role(Role.USER).build());
        User leader2 = userRepository.save(User.builder().username("leader2").password(passwordEncoder.encode("leader234!")).nickname("등산모임장").birthdate(LocalDate.of(1988, 5, 12)).role(Role.USER).build());
        User member1 = userRepository.save(User.builder().username("member1").password(passwordEncoder.encode("member123!")).nickname("정식멤버1").birthdate(LocalDate.of(1998, 7, 21)).role(Role.USER).build());
        User applicant1 = userRepository.save(User.builder().username("applicant1").password(passwordEncoder.encode("applicant123!")).nickname("신청자1").birthdate(LocalDate.of(2000, 11, 30)).role(Role.USER).build());
        User nonMember = userRepository.save(User.builder().username("non_member").password(passwordEncoder.encode("nonmember123!")).nickname("비회원").birthdate(LocalDate.of(1996, 8, 15)).role(Role.USER).build());


        // ==================== [3] 모임 및 멤버 데이터 생성 ====================

        // === 모임 A: 강남구 러닝 모임 (승인 완료, 멤버/신청자/일정 모두 있음) ===
        Group groupA = createGroup(
                "강남 저녁 러닝 크루", "매주 화,목 저녁에 함께 달릴 분들을 모집합니다. 초보 환영!",
                leader1, findRegionByName(regions, "강남구"), findSportByName(sports, "러닝"), 20, ApprovalStatus.APPROVED
        );
        // 모임 A 멤버 구성
        addMember(groupA, leader1, JoinStatus.APPROVED);
        addMember(groupA, member1, JoinStatus.APPROVED);
        addMember(groupA, applicant1, JoinStatus.PENDING);

        // === 모임 B: 마포구 자전거 모임 (승인 완료, 멤버만 있음) ===
        Group groupB = createGroup(
                "마포 한강 자전거 라이딩", "주말마다 한강따라 자전거 타실 분! 맛집 탐방은 덤!",
                member1, findRegionByName(regions, "마포구"), findSportByName(sports, "자전거"), 10, ApprovalStatus.APPROVED
        );
        addMember(groupB, member1, JoinStatus.APPROVED); // member1이 모임장
        addMember(groupB, leader1, JoinStatus.APPROVED); // leader1이 멤버로 참여


        // === 모임 C: 강북구 등산 모임 (승인 대기) ===
        Group groupC = createGroup(
                "북한산 둘레길 탐방대", "주말 오전에 북한산 등반하실 초보 등산러 모집합니다.",
                leader2, findRegionByName(regions, "강북구"), findSportByName(sports, "등산"), 15, ApprovalStatus.PENDING
        );
        addMember(groupC, leader2, JoinStatus.APPROVED);

        // === 모임 D: 송파구 축구 모임 (승인 완료, 멤버 없음) ===
        Group groupD = createGroup(
                "송파 아마추어 축구팀", "매주 토요일 아침, 함께 공 차실 분들을 모집합니다.",
                leader1, findRegionByName(regions, "송파구"), findSportByName(sports, "축구"), 22, ApprovalStatus.APPROVED
        );
        addMember(groupD, leader1, JoinStatus.APPROVED);

        // ==================== [4] 모임 일정 데이터 생성 ====================
        // 모임 A (강남 러닝)에 대한 일정 2개
        createSchedule(groupA, "양재천 영동3교 아래", LocalDateTime.now().plusDays(3).withHour(19).withMinute(30), "가볍게 5km 뛸 예정입니다. 물과 수건 챙겨오세요!");
        createSchedule(groupA, "선릉역 2번 출구", LocalDateTime.now().plusDays(5).withHour(20).withMinute(0), "이번주는 도심 런! 테헤란로를 따라 달립니다.");

        // 모임 B (마포 자전거)에 대한 일정 1개
        createSchedule(groupB, "망원 한강공원 나들목", LocalDateTime.now().plusDays(4).withHour(10).withMinute(0), "파주까지 다녀올 예정입니다. 약 4시간 코스.");
    }

    // --- Helper Methods ---
    private Region findRegionByName(List<Region> regions, String name) {
        return regions.stream().filter(r -> r.getRegionName().equals(name)).findFirst().orElseThrow();
    }

    private Sport findSportByName(List<Sport> sports, String name) {
        return sports.stream().filter(s -> s.getSportName().equals(name)).findFirst().orElseThrow();
    }

    private Group createGroup(String name, String desc, User leader, Region region, Sport sport, int maxMembers, ApprovalStatus status) {
        Group group = Group.builder()
                .groupName(name)
                .description(desc)
                .leader(leader)
                .region(region)
                .sport(sport)
                .maxMembers(maxMembers)
                .build();
        group.setApprovalStatus(status);
        return groupRepository.save(group);
    }

    private void addMember(Group group, User user, JoinStatus status) {
        groupMemberRepository.save(GroupMember.builder().group(group).user(user).status(status).build());
    }

    private void createSchedule(Group group, String location, LocalDateTime meetingTime, String description) {
        scheduleRepository.save(
                Schedule.builder()
                        .group(group)
                        .location(location)
                        .meetingTime(meetingTime)
                        .description(description)
                        .build()
        );
    }
}