package com.lms.api.common.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.api.admin.service.dto.LoginResponse;
import com.lms.api.common.auth.JwtTokenProvider;
import com.lms.api.common.dto.LoginType;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.RefreshTokenRepository;
import com.lms.api.common.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


/**
 * 소셜 로그인 성공 후 이동할 페이지를 커스터마이징하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.oauth.redirect-uri.user}")
    private String userRedirectUri;
    @Value("${spring.oauth.redirect-uri.admin}")
    private String adminRedirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");
        LoginType loginType = LoginType.valueOf((String) oAuth2User.getAttributes().get("loginType"));

        UserEntity userEntity = userRepository.findByEmailAndLoginType(email,loginType)
                .orElseThrow(() -> new RuntimeException("OAuth2로 가입된 유저 정보가 없음"));

        String accessToken = jwtTokenProvider.generateAccessToken(userEntity.getId(), userEntity.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getId());

        refreshTokenRepository.save(userEntity.getId(), refreshToken, userEntity.getLoginType());

        String baseRedirectUri = switch (userEntity.getRole()){
            case ADMIN -> adminRedirectUri;
            case USER -> userRedirectUri;
        };

        String redirectUrl  = UriComponentsBuilder.fromHttpUrl(baseRedirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("loginType", loginType)
                .queryParam("role", userEntity.getRole())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
        log.debug("✅ OAuth2 로그인 핸들러 종료");
    }
}
