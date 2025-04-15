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
