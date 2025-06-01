package com.lms.api.common.config;

import com.lms.api.admin.auth.oauth2.CustomOAuth2UserService;
import com.lms.api.admin.auth.oauth2.OAuth2SuccessHandler;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.admin.auth.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * - Cors 관련 설정
 *  -> 시큐리티 사용할거면 SecurityFilterChain 사용하고 아니면,
 *      CorsConfig 클래스 만들어서 corsConfigurationSource 메서드 사용
 *
 * -> Spring Security는 context-path를 포함한 경로 기준으로 requestMatchers를 평가
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable) // 시큐리티가 기본적으로 제공하는 엔드포인트를 사용하지 않을 때 사용
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                      .requestMatchers(
                              "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/login",
                              "/oauth2/**",
                              "/login/oauth2/**",
                              "/oauth2/authorization/**",
                              "/logout",
                              "/user",
                              "/test/**"
                              //"/api/test/**" 스프링시큐리티6버전부터는 기본 값 제외하고 매칭함!
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated() // 그 외는 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(
                        oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(((request, response, exception) -> {
                                    log.error("❌ OAuth2 로그인 실패: {}", exception.getMessage(), exception);
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"error\": \"OAuth2 login failed\"}");
                                }))
                )
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // ✅ 인증 정보(쿠키, 헤더 등) 포함 허용
        config.addAllowedHeader("*"); // ✅ 모든 헤더 허용
        config.addAllowedMethod("*"); // ✅ 모든 HTTP 메서드 허용
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:8081",
                "http://localhost:5173",
                "http://54.180.116.0:8080",
                "http://localhost:8084")); // ✅ 특정 Origin 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
