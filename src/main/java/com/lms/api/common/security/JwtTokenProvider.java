package com.lms.api.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

/**
 * JWT를 생성 하고 검증
 */
@Component
@Slf4j
@Getter
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.access-token-expiration}")
    private String accessTokenExpirationRaw;

    @Value("${spring.jwt.refresh-token-expiration}")
    private String refreshTokenExpirationRaw;

    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;

    @PostConstruct
    private void init() {
        this.accessTokenExpiration = parseDuration(accessTokenExpirationRaw);
        this.refreshTokenExpiration = parseDuration(refreshTokenExpirationRaw);
    }
    // Key 객체 생성
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    // JWT 토큰에서 Claims 추출 ( 토큰 안에 담긴 정보 )
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 서명 검증 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody();  // Claims 반환
    }

    // Access Token 생성
    public String generateAccessToken(String username){
        return Jwts.builder()
                .setSubject(username) // 사용자의 username을 subject로 설정
                .setIssuedAt(new Date())  // 발행 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration.toMillis())) // 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명
                .compact(); // JWT 생성
    }
    // Refresh Token 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 사용자의 username을 subject로 설정
                .setIssuedAt(new Date()) // 발행 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration.toMillis())) // 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명
                .compact(); // JWT 생성
    }

    // 토큰에서 사용자 정보(id) 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody()
                .getSubject(); // subject(사용자명) 반환
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 검증
            log.debug("✅ JWT 유효성 검증 성공");
            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            log.warn("⏳ JWT 만료됨: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("❌ JWT 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    // 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); 
        }
        return null;
    }

    public Authentication getAuthentication(String username) {
        return new UsernamePasswordAuthenticationToken(username, null, null);
    }

    private Duration parseDuration(String value) {
        value = value.trim().toLowerCase();
        if (value.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(value.replace("s", "")));
        } else if (value.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(value.replace("m", "")));
        } else if (value.endsWith("h")) {
            return Duration.ofHours(Long.parseLong(value.replace("h", "")));
        } else if (value.endsWith("d")) {
            return Duration.ofDays(Long.parseLong(value.replace("d", "")));
        } else {
            throw new IllegalArgumentException("지원하지 않는 시간 포맷입니다: " + value);
        }
    }
}

/**
 * UsernamePasswordAuthenticationToken 객체
 * -> 스프링 시큐리티에서 기본 인증 객체로 권한이 필요 없을 때 null 로 넘기고 추후 Role이 필요하다면 추가할 수 있음.
 */