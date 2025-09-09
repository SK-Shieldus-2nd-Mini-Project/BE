package com.rookies4.MiniProject2.config;

import com.rookies4.MiniProject2.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 이 import 문을 추가합니다.
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 로그인/회원가입, 지역/종목 조회는 인증 없이 접근 허용
                        .requestMatchers("/api/auth/**", "/api/regions", "/api/sports").permitAll()

                        // 2. 모임 생성은 USER 권한이 필요합니다.
                        .requestMatchers(HttpMethod.POST, "/api/groups").hasRole("USER") // 수정된 부분입니다.

                        // 3. 모임 조회는 모든 인증된 사용자가 접근 가능합니다.
                        .requestMatchers(HttpMethod.GET, "/api/groups").authenticated() // 수정된 부분입니다.

                        // 4. 관리자 API는 ADMIN 권한이 필요합니다.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 5. 그 외 모든 요청은 인증된 사용자만 접근 가능합니다.
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}