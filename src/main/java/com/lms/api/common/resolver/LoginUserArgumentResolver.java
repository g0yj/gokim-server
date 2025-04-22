package com.lms.api.common.resolver;

import com.lms.api.common.dto.LoginUser;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Profile({"local","dev"})
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().equals(UserEntity.class);
    }

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

}
