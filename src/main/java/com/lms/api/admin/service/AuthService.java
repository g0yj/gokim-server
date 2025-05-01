package com.lms.api.admin.service;

import com.lms.api.admin.controller.dto.LoginRequest;
import com.lms.api.admin.service.dto.LoginResponse;
import com.lms.api.admin.service.dto.NewTokenResponse;
import com.lms.api.common.dto.LoginType;
import com.lms.api.common.dto.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.RefreshTokenRepository;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.UserType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request){
        UserEntity userEntity = userRepository.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())){
            log.warn("⚠️ 로그인 실패 - 비밀번호 불일치");
            throw new ApiException(ApiErrorCode.CHANGEPW1_SERVER_ERROR);
        }
        UserRole userRole = userEntity.getRole();
        String accessToken = jwtTokenProvider.generateAccessToken(userEntity.getId(), userRole);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getId());


        refreshTokenRepository.save(userEntity.getId(),refreshToken, LoginType.NORMAL);
        log.debug("✅ 로그인 성공 userId={}, accessToken={}, refreshToken={}", userEntity.getId(), accessToken, refreshToken);


        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(userEntity.getRole())
                .loginType(LoginType.NORMAL)
                .build();
    }


    @Transactional
    public NewTokenResponse refresh(HttpServletRequest request){
        String refreshToken = jwtTokenProvider.resolveToken(request);
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        String id = jwtTokenProvider.getUsernameFromToken(refreshToken);

        String savedRefreshToken = refreshTokenRepository.findRefreshTokenByUserId(id);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        UserRole userRole = userRepository.findById(id)
                .map(UserEntity::getRole)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(id, userRole);

        return NewTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }


}



