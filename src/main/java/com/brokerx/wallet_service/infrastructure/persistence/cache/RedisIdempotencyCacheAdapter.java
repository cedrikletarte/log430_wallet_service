package com.brokerx.wallet_service.infrastructure.persistence.cache;

import com.brokerx.wallet_service.application.port.out.IdempotencyCachePort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis adapter implementing the IdempotencyCachePort
 * This is an outbound adapter in hexagonal architecture
 */
@Slf4j
@Service
public class RedisIdempotencyCacheAdapter implements IdempotencyCachePort {
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:wallet:";
    private static final Duration IDEMPOTENCY_KEY_TTL = Duration.ofHours(24); // 24 hours TTL
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisIdempotencyCacheAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isDuplicate(String idempotencyKey, Long userId) {
        String redisKey = buildRedisKey(idempotencyKey, userId);
        Boolean exists = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(exists)) {
            log.warn("Duplicate wallet request detected: idempotencyKey={}, userId={}", idempotencyKey, userId);
            return true;
        }
        return false;
    }

    @Override
    public Object getCachedResponse(String idempotencyKey, Long userId) {
        String redisKey = buildRedisKey(idempotencyKey, userId);
        return redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public void storeResponse(String idempotencyKey, Long userId, Object response) {
        String redisKey = buildRedisKey(idempotencyKey, userId);
        redisTemplate.opsForValue().set(redisKey, response, IDEMPOTENCY_KEY_TTL.toMillis(), TimeUnit.MILLISECONDS);
        log.info("Stored idempotency key: key={}, userId={}, ttl={}h", idempotencyKey, userId, IDEMPOTENCY_KEY_TTL.toHours());
    }

    private String buildRedisKey(String idempotencyKey, Long userId) {
        return IDEMPOTENCY_KEY_PREFIX + userId + ":" + idempotencyKey;
    }
}
