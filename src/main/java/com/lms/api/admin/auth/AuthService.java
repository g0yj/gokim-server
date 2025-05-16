package com.lms.api.admin.auth;

import com.lms.api.admin.auth.dto.LoginRequest;
import com.lms.api.admin.auth.dto.LoginResponse;
import com.lms.api.admin.auth.dto.NewTokenResponse;
import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.RefreshTokenRepository;
import com.lms.api.common.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;

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
    public NewTokenResponse refresh(HttpServletRequest request) {
        // 1. 헤더에서 토큰 추출
        String refreshToken = request.getHeader("RefreshToken");
        String accessToken = jwtTokenProvider.resolveToken(request);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("❌ 유효하지 않은 Refresh Token입니다.");
        }

        // 2. accessToken이 블랙리스트에 있다면 → 이미 로그아웃된 사용자
        if (Boolean.TRUE.equals(redisTemplate.hasKey(accessToken))) {
            throw new RuntimeException("🚫 로그아웃된 사용자입니다.");
        }

        // 3. 사용자 ID 추출
        String userId = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // 4. Redis에서 저장된 refreshToken과 일치 확인
        String savedRefreshToken = refreshTokenRepository.findRefreshTokenByUserId(userId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("🔑 저장된 Refresh Token과 일치하지 않습니다.");
        }

        // 5. 사용자 권한 조회
        UserRole userRole = userRepository.findById(userId)
                .map(UserEntity::getRole)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        // 6. accessToken 재발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, userRole);

        return NewTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional
    public void logout(HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveToken(request);
        if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
            throw new SecurityException("유효 하지 않거나 누락된 JWT 토큰");
        }

        String userId = jwtTokenProvider.getUsernameFromToken(accessToken);

        // accessToken 만료 시간 계산 (ms 단위)
        long expiration = jwtTokenProvider.getExpiration(accessToken);

        // accessToken → 블랙리스트 등록
        String blacklistKey = "blacklist:" + accessToken;
        redisTemplate.opsForValue().set(blacklistKey, "logout", expiration, TimeUnit.MILLISECONDS);
        log.info("🛑 accessToken 블랙리스트 등록 - key: {}, TTL: {}ms", blacklistKey, expiration);

        //  RefreshToken(및 loginType) → Redis에서 삭제
        refreshTokenRepository.delete(userId);
        log.info("🗑️ Redis에서 refreshToken 삭제 완료 - userId: {}", userId);

        // 로그아웃 완료 로그
        log.info("✅ 로그아웃 완료 - userId: {}, accessToken 남은 시간: {}ms", userId, expiration);
    }
}



