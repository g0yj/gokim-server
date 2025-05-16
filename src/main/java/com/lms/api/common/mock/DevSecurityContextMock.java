package com.lms.api.common.mock;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
public class DevSecurityContextMock {
    @PostConstruct
    public void mockSecurityContext(){
        String fakeUserId = "SampleOwner";
        Authentication auth = new UsernamePasswordAuthenticationToken(fakeUserId, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}

/**
 * 목적과 동작:
 * 개발 및 테스트 환경에서 인증을 모킹하기 위해서:
 *
 * 실제로 Spring Security의 인증 시스템을 설정하지 않고도, 개발 중에 인증된 사용자 정보를 사용하고자 할 때 사용됩니다. 예를 들어, 개발 환경에서 로그인 없이도 SecurityContext에 가짜 사용자 정보를 설정하여 인증된 상태로 코드를 실행할 수 있습니다.
 *
 * SecurityContext:
 *
 * Spring Security는 요청을 처리할 때마다 SecurityContextHolder를 사용하여 현재 인증된 사용자 정보를 저장합니다. 이 정보는 보통 로그인 절차 후에 세션이나 JWT 토큰을 통해 설정됩니다. 하지만 개발 환경에서는 복잡한 인증 절차를 생략하고 간단하게 모킹할 수 있습니다.
 *
 * 가짜 인증 설정:
 *
 * mockSecurityContext 메서드는 개발 환경에서 가짜 사용자 정보를 SecurityContext에 설정하여, 후속 코드가 인증된 사용자로 동작하도록 만듭니다. 여기서 fakeUserId = "SampleOwner"는 가짜 사용자 ID입니다.
 *
 * UsernamePasswordAuthenticationToken은 사용자 이름(fakeUserId)과 비밀번호를 인수로 받아서 인증 객체를 생성합니다. 비밀번호는 null로 설정되어 있습니다. 실제 환경에서는 비밀번호를 검사하지만, 여기서는 이를 모킹하기 위해 비밀번호를 사용하지 않습니다.
 *
 * SecurityContextHolder.getContext().setAuthentication(auth); 코드를 통해 Authentication 객체를 SecurityContext에 설정합니다.
 */