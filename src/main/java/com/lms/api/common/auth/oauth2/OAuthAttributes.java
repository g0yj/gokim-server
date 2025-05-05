package com.lms.api.common.auth.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 소셜 사용자 정보 추출
 */
@RequiredArgsConstructor
@Getter@Setter
public class OAuthAttributes {
    private final String email;
    private final String name;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId.toLowerCase()){
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new RuntimeException("제공 하지 않는 OAuth2 provider 입니다 : " +registrationId);
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
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return new OAuthAttributes(
                (String) account.get("email"),
                (String) profile.get("nickname")
        );
    }
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return new OAuthAttributes(
                (String) response.get("email"),
                (String) response.get("name")
        );
    }
}
