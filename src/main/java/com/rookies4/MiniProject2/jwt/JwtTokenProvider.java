package com.rookies4.MiniProject2.jwt;

import com.rookies4.MiniProject2.dto.AuthDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L; // 1시간

    @Value("${security.jwt.token.secret-key}") // application.properties 에서 secret key 가져오기
    private String key;
    private SecretKey secretKey;

    @PostConstruct
    public void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    // Authentication 객체를 받아 Access Token 생성
    public AuthDto.TokenResponse generateToken(Authentication authentication) {
        // 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        // Access Token 생성
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();

        return AuthDto.TokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiredDate.getTime())
                .build();
    }

    // JWT 토큰을 디코딩 -> 토큰에 들어있는 정보 꺼내기
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("[ERROR] 권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // 토큰 정보 검즘하기
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { // TODO: Exception 유형 구체화하기, 로그 남기기
            return false;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
