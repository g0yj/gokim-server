package com.lms.api.common.repository;

import com.lms.api.admin.auth.enums.LoginType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;

/**
 * Redis 저장용 Repository
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "refresh:";

    @Value("${token.refresh-token-ttl}")
    private String refreshTokenTtlRaw;

    private Duration refreshTokenTtl;


    @PostConstruct
    private void init() {
        if (refreshTokenTtlRaw.endsWith("d")) {
            refreshTokenTtl = Duration.ofDays(Long.parseLong(refreshTokenTtlRaw.replace("d", "")));
        } else if (refreshTokenTtlRaw.endsWith("h")) {
            refreshTokenTtl = Duration.ofHours(Long.parseLong(refreshTokenTtlRaw.replace("h", "")));
        } else if (refreshTokenTtlRaw.endsWith("m")) {
            refreshTokenTtl = Duration.ofMinutes(Long.parseLong(refreshTokenTtlRaw.replace("m", "")));
        } else if (refreshTokenTtlRaw.endsWith("s")) {
            refreshTokenTtl = Duration.ofSeconds(Long.parseLong(refreshTokenTtlRaw.replace("s", "")));
        } else {
            throw new IllegalArgumentException("지원하지 않는 TTL 단위입니다: " + refreshTokenTtlRaw);
        }
    }
    @PostConstruct
    public void postConstructCheck() {
        log.debug("🔧 RedisTemplate null 체크: {}", redisTemplate != null ? "✅ 주입됨" : "❌ 주입 안 됨");
    }

    // 🔁 확장된 저장: refreshToken + loginType
    public void save(String userId, String refreshToken, LoginType loginType) {
        log.debug("🟢 Redis에 저장 시작 - userId={}, refreshToken={}, loginType={}", userId, refreshToken, loginType);

        String key = PREFIX + userId;

        // 기존 키 삭제 (타입 충돌 방지)
        redisTemplate.delete(key);

        Map<String, Object> values = Map.of(
                "refreshToken", refreshToken,
                "loginType", loginType
        );
        redisTemplate.opsForHash().putAll(PREFIX + userId, values);
        redisTemplate.expire(PREFIX + userId, refreshTokenTtl);
    }

    // 🔍 refreshToken만 조회
    public String findRefreshTokenByUserId(String userId) {
        Object value = redisTemplate.opsForHash().get(PREFIX + userId, "refreshToken");
        return value != null ? value.toString() : null;
    }

    // loginType만 조회
    public String findLoginTypeByUserId(String userId) {
        Object value = redisTemplate.opsForHash().get(PREFIX + userId, "loginType");
        return value != null ? value.toString() : null;
    }

    // 삭제
    public void delete(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
