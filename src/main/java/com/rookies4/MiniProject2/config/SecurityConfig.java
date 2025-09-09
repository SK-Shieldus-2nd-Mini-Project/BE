package com.rookies4.MiniProject2.config;

import com.rookies4.MiniProject2.jwt.JwtAuthenticationFilter;
import com.rookies4.MiniProject2.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@EnableWebSecurity //스프링 시큐리티 활성화
@EnableMethodSecurity // @PreAuthorize 어노테이션 사용을 위함
public class SecurityConfig {

    // 직접 만든 JWT 관련 클래스들
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (Stateless 서버이기 때문)
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화 (JWT 는 자체 인증 방식 사용)
                .formLogin(formLogin -> formLogin.disable()) // Form Login 비활성화

                // 세션 관리 방식 -> Stateless (세션 사용 안 함)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                // HTTP 요청에 대한 인가(Authorization) 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // API 명세에 따른 경로별 접근 권한 설정
                        // 1. 인증 없이 접근 가능한 경로
                        // TODO: 메인페이지 엔드포인트 뭔지 알아내기
                        .requestMatchers("/api/auth/**", "/api/regions", "/api/sports", "/api/groups").permitAll()
                        // 2. ADMIN 역할을 가진 사용자만 접근 가능한 경로
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 3. 그 외 모든 요청은 인증된 사용자만 접근 가능.
                        .anyRequest().authenticated()
                )

                // 직접 만든 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}