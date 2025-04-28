package com.lms.api.common.config;

import com.lms.api.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 나중에 Spring Security 도입하면 → FilterConfig 지우고 SecurityFilterChain에 통합
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FilterConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        log.debug("jwtFilter 빈 등록 확인");
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthenticationFilter); // 필터를 거치지 않아야하는 url에 있다면 JwtAuthenticationFilter 클래스에 추가.
        registrationBean.addUrlPatterns("/api/*"); // 하위 요청에만 필터 적용
        registrationBean.setOrder(1); // 필터 순서 (낮을수록 먼저 실행)
        return registrationBean;
    }



}


