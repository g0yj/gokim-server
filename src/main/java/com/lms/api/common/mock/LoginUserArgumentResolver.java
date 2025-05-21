package com.lms.api.common.mock;

import com.lms.api.admin.auth.LoginUser;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.admin.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 컨트롤러 메서드의 파라미터를 자동으로 해석해서 값을 넣어주는 컴포넌트
 */
@Component
@RequiredArgsConstructor
@Profile({"local", "dev", "prod"}) // local, dev 환경에서만 동작
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && UserEntity.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        String token = extractToken(webRequest);
        if (token == null) {
            // ❌ 토큰 없으면 401 에러 던지기
            throw new RuntimeException("로그인이 필요합니다 (Authorization 헤더 없음)");
        }

/*
        if (token == null) {
            // 토큰이 없는 경우: 테스트용 계정 반환
            return findTestUser();
        }
*/

        // 토큰이 있을 경우: 유저 ID 추출
        String userId;
        try {
            userId = jwtTokenProvider.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }

        // 사용자 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
    }

    private String extractToken(NativeWebRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제외하고 추출
        }
        return null;
    }

    private UserEntity findTestUser() {
        final String testUserId = "SampleMember3"; // 테스트용 ID
        return userRepository.findById(testUserId)
                .orElseThrow(() -> new RuntimeException("테스트 유저를 찾을 수 없습니다: " + testUserId));
    }
}

/** 시큐리티 사용할 경우,
 *
 @Override
 public Object resolveArgument(MethodParameter parameter,
 ModelAndViewContainer mavContainer,
 NativeWebRequest webRequest,
 WebDataBinderFactory binderFactory) {

 var authentication = SecurityContextHolder.getContext().getAuthentication();

 // 인증이 없거나 anonymousUser일 경우 테스트 유저로 대체
 if (authentication == null ||
 !authentication.isAuthenticated() ||
 authentication.getPrincipal().equals("anonymousUser")) {

 // 테스트 유저 ID로 대체 (DB에 있는 값이어야 함)
 final String testUserId = "SampleOwner";

 return userRepository.findById(testUserId)
 .orElseThrow(() -> new RuntimeException("테스트 유저가 없습니다: " + testUserId));
 }

 String userId = (String) authentication.getPrincipal();

 return userRepository.findById(userId)
 .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + userId));
 }
 */