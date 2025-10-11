package com.brokerx.wallet_service.infrastructure.seeder;

import java.math.BigDecimal;

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

    @Bean
    CommandLineRunner seedWallet(WalletRepositoryAdapter walletRepository) {
        return args -> {
            if (walletRepository.findById(1L).isEmpty()) {
                Wallet wallet = Wallet.builder()
                        .userId(1L)
                        .balance(BigDecimal.valueOf(1000))
                        .currency("USD")
                        .build();
                walletRepository.save(wallet);
                log.info("Wallet created for user ID {}: {}", wallet.getUserId(), wallet.getBalance());
            }
        };
    }
}
