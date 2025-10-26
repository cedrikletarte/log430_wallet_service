package com.brokerx.wallet_service.infrastructure.seeder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.infrastructure.persistence.repository.wallet.WalletRepositoryAdapter;

@Configuration
@Order(1)
public class WalletSeeder {

    private static final Logger log = LoggerFactory.getLogger(WalletSeeder.class);
    private static final int TEST_USERS_COUNT = 50;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000000000); // 1B per trader

    @Bean
    CommandLineRunner seedWallet(WalletRepositoryAdapter walletRepository) {
        return args -> {
            List<Wallet> walletsToCreate = new ArrayList<>();
            
            // 1. Create wallet for admin (userId = 1)
            if (walletRepository.findByUserId(1L).isEmpty()) {
                Wallet adminWallet = Wallet.builder()
                        .userId(1L)
                        .availableBalance(BigDecimal.valueOf(1000))
                        .reservedBalance(BigDecimal.ZERO)
                        .currency("USD")
                        .build();
                walletRepository.save(adminWallet);
                log.info("✅ Admin wallet created: {}", adminWallet.getId());
            }
            
            // 2. Create wallets for test traders (userId = 2 to 31)
            for (long userId = 2; userId <= TEST_USERS_COUNT + 1; userId++) {
                if (walletRepository.findByUserId(userId).isEmpty()) {
                    Wallet traderWallet = Wallet.builder()
                            .userId(userId)
                            .availableBalance(INITIAL_BALANCE)
                            .reservedBalance(BigDecimal.ZERO)
                            .currency("USD")
                            .build();
                    walletsToCreate.add(traderWallet);
                }
            }
            
            // Save all new wallets in batch
            if (!walletsToCreate.isEmpty()) {
                walletRepository.saveAll(walletsToCreate);
                log.info("✅ Created {} test wallets (trader1@test.com to trader{}@test.com)", 
                         walletsToCreate.size(), TEST_USERS_COUNT);
            } else {
                log.info("ℹ️ Wallets already exist");
            }
        };
    }
}