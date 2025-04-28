package com.lms.api.common.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;
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


    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(PREFIX + userId, refreshToken, refreshTokenTtl);
    }

    public String findByUserId(String userId) {
        return redisTemplate.opsForValue()
                .get(PREFIX + userId);
    }

    public void delete(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}
