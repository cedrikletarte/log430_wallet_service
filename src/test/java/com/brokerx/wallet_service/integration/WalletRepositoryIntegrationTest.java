package com.brokerx.wallet_service.integration;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.mapper.WalletMapper;
import com.brokerx.wallet_service.infrastructure.persistence.repository.wallet.WalletRepositoryAdapter;

@Testcontainers
@DataJpaTest
@Import({WalletRepositoryAdapter.class, WalletMapper.class})
class WalletRepositoryIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private WalletRepositoryAdapter walletRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldSaveAndRetrieveWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setAvailableBalance(BigDecimal.valueOf(1000.0));
        wallet.setReservedBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD");

        walletRepository.save(wallet);

        Wallet found = walletRepository.findByUserId(1L).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(1000.0).compareTo(found.getAvailableBalance()));
    }

    @Test
    void shouldCreditWalletBalance() {
        Wallet wallet = new Wallet();
        wallet.setUserId(2L);
        wallet.setAvailableBalance(BigDecimal.valueOf(500.0));
        wallet.setCurrency("USD");

        walletRepository.save(wallet);

        Wallet found = walletRepository.findByUserId(2L).orElseThrow();
        found.credit(BigDecimal.valueOf(200.0));
        walletRepository.save(found);

        Wallet updated = walletRepository.findByUserId(2L).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(700.0).compareTo(updated.getAvailableBalance()));
    }

    @Test
    void shouldDebitWalletBalance() {
        Wallet wallet = new Wallet();
        wallet.setUserId(3L);
        wallet.setAvailableBalance(BigDecimal.valueOf(800.0));
        wallet.setCurrency("USD");

        walletRepository.save(wallet);

        Wallet found = walletRepository.findByUserId(3L).orElseThrow();
        found.debit(BigDecimal.valueOf(300.0));
        walletRepository.save(found);

        Wallet updated = walletRepository.findByUserId(3L).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(500.0).compareTo(updated.getAvailableBalance()));
    }
}
