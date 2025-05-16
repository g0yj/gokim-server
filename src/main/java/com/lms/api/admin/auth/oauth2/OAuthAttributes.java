package com.lms.api.admin.auth.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 소셜 사용자 정보 추출
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public class OAuthAttributes {

    private final String email;
    private final String name;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        };
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes){
        return new OAuthAttributes(
                (String)attributes.get("email"),
                (String)attributes.get("name")
        );
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        log.debug("account :{}", account);
        if (account == null) {
            throw new IllegalArgumentException("Kakao account 정보가 없습니다.");
        }
        String email = (String) account.get("email");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        String nickname = (String) profile.getOrDefault("nickname", "카카오 사용자");

        return new OAuthAttributes(email, nickname);
    }



    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return new OAuthAttributes(
                (String) response.get("email"),
                (String) response.getOrDefault("name", "네이버 사용자")
        );
    }
}
