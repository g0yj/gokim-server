package com.lms.api.common.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

/**
 * Spring Security를 사용 중이라면 SecurityFilterChain에 .cors()를 추가하여 CORS 설정을 활성화해야함
 */
@Configuration
public class CorsConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 필터 체인에서 가장 먼저 실행
    public Filter corsFilter() {
        System.out.println("✅✅✅ CorsFilter 등록됨");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173",
                "http://localhost:8081",
                "http://localhost:8084",
                "https://0aa8-58-228-2-217.ngrok-free.app"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
/*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // or "/**" 전체 경로 허용도 가능
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:8081",
                        "http://localhost:8084",
                        "https://0aa8-58-228-2-217.ngrok-free.app"
                )
                .allowedMethods("*")       // GET, POST, PUT, DELETE 등 모두 허용
                .allowedHeaders("*")       // 모든 헤더 허용
                .allowCredentials(true);   // 쿠키/세션 포함 허용
    }
*/

/*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // ✅ 인증 정보(쿠키, 헤더 등) 포함 허용
        config.addAllowedHeader("*"); // ✅ 모든 헤더 허용
        config.addAllowedMethod("*"); // ✅ 모든 HTTP 메서드 허용
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:8081",
                "http://localhost:5173",
                "https://*.ngrok-free.app",
                "http://localhost:8084")); // ✅ 특정 Origin 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
*/

}
