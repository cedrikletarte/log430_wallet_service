package com.brokerx.wallet_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.brokerx.wallet_service.infrastructure.config.RedisConfig;
import com.brokerx.wallet_service.infrastructure.persistence.cache.RedisIdempotencyCacheAdapter;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.TransactionStatus;

@Testcontainers
@DataRedisTest
@Import({RedisIdempotencyCacheAdapter.class, RedisConfig.class})
class RedisIdempotencyIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private RedisIdempotencyCacheAdapter idempotencyService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        // Nettoyer Redis avant chaque test
        var connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory != null) {
            connectionFactory.getConnection().serverCommands().flushAll();
        }
    }

    @Test
    void shouldDetectNoDuplicateForNewKey() {
        String idempotencyKey = "unique-key-123";
        Long userId = 1L;

        boolean isDuplicate = idempotencyService.isDuplicate(idempotencyKey, userId);
        assertFalse(isDuplicate, "New idempotency key should not be detected as duplicate");
    }

    @Test
    void shouldDetectDuplicateAfterStoringResponse() {
        String idempotencyKey = "test-key-456";
        Long userId = 2L;
        TransactionSuccess response = TransactionSuccess.builder()
            .id(100L)
            .type(TransactionType.CREDIT)
            .status(TransactionStatus.PENDING)
            .amount(BigDecimal.TEN)
            .currency("USD")
            .isSettled(false)
            .build();

        idempotencyService.storeResponse(idempotencyKey, userId, response);
        boolean isDuplicate = idempotencyService.isDuplicate(idempotencyKey, userId);
        assertTrue(isDuplicate, "Idempotency key should be detected as duplicate after storing");
    }

    @Test
    void shouldStoreAndRetrieveResponse() {
        String idempotencyKey = "response-key-789";
        Long userId = 3L;
        TransactionSuccess expectedResponse = TransactionSuccess.builder()
            .id(200L)
            .type(TransactionType.DEBIT)
            .status(TransactionStatus.SETTLED)
            .amount(new BigDecimal("50.00"))
            .currency("USD")
            .isSettled(true)
            .build();

        idempotencyService.storeResponse(idempotencyKey, userId, expectedResponse);
        Object cachedResponse = idempotencyService.getCachedResponse(idempotencyKey, userId);
        assertNotNull(cachedResponse, "Cached response should not be null");
        assertTrue(cachedResponse instanceof TransactionSuccess, "Cached response should be a TransactionSuccess");
        TransactionSuccess retrieved = (TransactionSuccess) cachedResponse;
        assertEquals(200L, retrieved.getId());
        assertEquals(TransactionType.DEBIT, retrieved.getType());
        assertEquals(TransactionStatus.SETTLED, retrieved.getStatus());
        assertEquals(new BigDecimal("50.00"), retrieved.getAmount());
        assertEquals("USD", retrieved.getCurrency());
        assertTrue(retrieved.isSettled());
    }

    @Test
    void shouldReturnNullForNonExistentKey() {
        String idempotencyKey = "non-existent-key";
        Long userId = 4L;
        Object response = idempotencyService.getCachedResponse(idempotencyKey, userId);
        assertNull(response, "Response should be null for non-existent key");
    }

    @Test
    void shouldIsolateDifferentUserIds() {
        String idempotencyKey = "same-key";
        Long userId1 = 5L;
        Long userId2 = 6L;
        TransactionSuccess response1 = TransactionSuccess.builder().id(300L).type(TransactionType.CREDIT).status(TransactionStatus.SETTLED).amount(BigDecimal.ONE).currency("USD").isSettled(true).build();
        TransactionSuccess response2 = TransactionSuccess.builder().id(400L).type(TransactionType.DEBIT).status(TransactionStatus.SETTLED).amount(BigDecimal.TEN).currency("USD").isSettled(true).build();
        idempotencyService.storeResponse(idempotencyKey, userId1, response1);
        idempotencyService.storeResponse(idempotencyKey, userId2, response2);
        assertTrue(idempotencyService.isDuplicate(idempotencyKey, userId1));
        assertTrue(idempotencyService.isDuplicate(idempotencyKey, userId2));
        TransactionSuccess cached1 = (TransactionSuccess) idempotencyService.getCachedResponse(idempotencyKey, userId1);
        TransactionSuccess cached2 = (TransactionSuccess) idempotencyService.getCachedResponse(idempotencyKey, userId2);
        assertEquals(300L, cached1.getId());
        assertEquals(400L, cached2.getId());
    }

    @Test
    void shouldHandleMultipleIdempotencyKeys() {
        Long userId = 7L;
        TransactionSuccess response1 = TransactionSuccess.builder().id(500L).type(TransactionType.CREDIT).status(TransactionStatus.SETTLED).amount(BigDecimal.ONE).currency("USD").isSettled(true).build();
        TransactionSuccess response2 = TransactionSuccess.builder().id(600L).type(TransactionType.DEBIT).status(TransactionStatus.SETTLED).amount(BigDecimal.TEN).currency("USD").isSettled(true).build();
        TransactionSuccess response3 = TransactionSuccess.builder().id(700L).type(TransactionType.CREDIT).status(TransactionStatus.SETTLED).amount(BigDecimal.valueOf(5)).currency("USD").isSettled(true).build();
        idempotencyService.storeResponse("key-1", userId, response1);
        idempotencyService.storeResponse("key-2", userId, response2);
        idempotencyService.storeResponse("key-3", userId, response3);
        assertTrue(idempotencyService.isDuplicate("key-1", userId));
        assertTrue(idempotencyService.isDuplicate("key-2", userId));
        assertTrue(idempotencyService.isDuplicate("key-3", userId));
        assertFalse(idempotencyService.isDuplicate("key-4", userId));
    }

    @Test
    void shouldOverwriteExistingIdempotencyKey() {
        String idempotencyKey = "overwrite-key";
        Long userId = 8L;
        TransactionSuccess oldResponse = TransactionSuccess.builder().id(800L).type(TransactionType.CREDIT).status(TransactionStatus.PENDING).amount(BigDecimal.ONE).currency("USD").isSettled(false).build();
        TransactionSuccess newResponse = TransactionSuccess.builder().id(800L).type(TransactionType.CREDIT).status(TransactionStatus.SETTLED).amount(BigDecimal.ONE).currency("USD").isSettled(true).build();
        idempotencyService.storeResponse(idempotencyKey, userId, oldResponse);
        TransactionSuccess cached1 = (TransactionSuccess) idempotencyService.getCachedResponse(idempotencyKey, userId);
        assertEquals(TransactionStatus.PENDING, cached1.getStatus());
        idempotencyService.storeResponse(idempotencyKey, userId, newResponse);
        TransactionSuccess cached2 = (TransactionSuccess) idempotencyService.getCachedResponse(idempotencyKey, userId);
        assertEquals(TransactionStatus.SETTLED, cached2.getStatus());
    }

    @Test
    void shouldNotDetectDuplicateAcrossDifferentUsers() {
        String idempotencyKey = "shared-key";
        Long userId1 = 12L;
        Long userId2 = 13L;
        TransactionSuccess response = TransactionSuccess.builder().id(1000L).type(TransactionType.CREDIT).status(TransactionStatus.SETTLED).amount(BigDecimal.ONE).currency("USD").isSettled(true).build();
        idempotencyService.storeResponse(idempotencyKey, userId1, response);
        assertTrue(idempotencyService.isDuplicate(idempotencyKey, userId1));
        assertFalse(idempotencyService.isDuplicate(idempotencyKey, userId2));
    }
}
