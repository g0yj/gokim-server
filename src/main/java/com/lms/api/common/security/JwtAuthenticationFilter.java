package com.lms.api.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 토큰을 검증하는 필터
 * -> 토큰이 각 요청에 대해 유효한지 확인하는 필터.
 * -> Authorization 헤더에 있는 JWT 토큰을 검증하고, 토큰이 유효하면 인증 정보를 담은 사용자 객체를 생성
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired JwtTokenProvider jwtTokenProvider;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.debug("🔎 현재 요청 path = {}", path);
        return EXCLUDE_URLS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        log.debug("✅ jwt 필터에서 token = {} ", token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            //  정상 토큰이면 인증 객체 생성해서 저장
            String userId = jwtTokenProvider.getUsernameFromToken(token);
            Authentication authentication = jwtTokenProvider.getAuthentication(userId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("✅ 인증 성공. SecurityContextHolder 저장 완료!");

            // 🔥 다음 필터로 진행
            filterChain.doFilter(request, response);

        } else {
            log.error("❌ 유효하지 않은 토큰이거나 토큰이 없습니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\": \"Unauthorized - Invalid or Missing Token\"}");

            return;
        }
    }

}
