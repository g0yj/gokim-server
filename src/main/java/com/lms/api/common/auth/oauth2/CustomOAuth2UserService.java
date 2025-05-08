package com.lms.api.common.auth.oauth2;

import com.lms.api.common.dto.LoginType;
import com.lms.api.common.dto.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
            log.debug("기본 OAuth2User 정보: {}" , oAuth2User);
        // 2. 로그인 타입 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = LoginType.valueOf(registrationId.toUpperCase());
        // 3. 사용자 식별키 가져 오기
        String userNameAttr = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        // 4. 사용자 정보 통합 객체 생성
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());
        log.debug("attributes :{} ", attributes);
        String email = attributes.getEmail();
        String name = attributes.getName();

        if(email == null){
            throw new OAuth2AuthenticationException("소셜 로그인에 Email 정보가 없습니다");
        }
        // 5. 유저 조회 또는 신규 생성
        UserEntity userEntity = userRepository.findByEmailAndLoginType(email, loginType)
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .id("ID" + System.nanoTime())
                                .email(email)
                                .name(name)
                                .loginType(loginType)
                                .role(UserRole.USER)
                                .build()
                ));

        // 6. 시큐리티 사용자 반환
        Map<String, Object> customAttributes = new HashMap<>(oAuth2User.getAttributes());
        customAttributes.put("loginType", loginType.name());
        customAttributes.put("email", email);
        customAttributes.put("name", name);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole())),
                customAttributes,
                userNameAttr
        );
    }


}
