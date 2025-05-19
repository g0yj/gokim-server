package com.lms.api.admin.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하는 필터
 * -> 토큰이 각 요청에 대해 유효한지 확인하는 필터.
 * -> Authorization 헤더에 있는 JWT 토큰을 검증하고, 토큰이 유효하면 인증 정보를 담은 사용자 객체를 생성
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // ✅ JWT 인증을 건너뛰는 예외 경로 목록
        if (uri.startsWith("/api/test")){
            log.debug("🔓 JWT 필터 예외 경로: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);
        log.debug("✅ jwt 필터에서 token = {}", token);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            // ✅ 블랙리스트 확인 (prefix 포함)
            String blacklistKey = "blacklist:" + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                log.warn("⛔️ 블랙리스트 토큰 거부 - {}", token);
                throw new SecurityException("로그아웃된 토큰입니다.");
            }

            //  토큰이 유효하면 인증 객체 생성해서 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.debug("토큰이 없거나 유효 하지 않음. 필터 통과만 수행.");
        }

        // 무조건 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

}
