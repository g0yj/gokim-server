package com.lms.api.common.exception;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupValidator {

    private final Environment env;
    @PostConstruct
    public void validate() {
        validateActiveProfile();
        validateJwtSecret();
        validateAwsCredentials();
    }

    private void validateActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) {
            throw new IllegalStateException("❌ spring.profiles이 지정되지 않음");
        }
    }

    private void validateJwtSecret() {
        String jwtSecret = env.getProperty("spring.security.jwt.secret");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("❌ JWT 서명 키(security.jwt.secret)가 누락됨.");
        }
    }

    private void validateAwsCredentials() {
        if (env.acceptsProfiles("dev")) {
            if (env.getProperty("cloud.aws.credentials.access-key") == null) {
                throw new IllegalStateException("❌ AWS Access Key가 설정되지 않음. EC2 서버가 아닐 때 S3 사용을 위해서는 key가 반드시 필요");
            }
        }
    }
}
