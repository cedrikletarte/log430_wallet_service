package com.brokerx.wallet_service.adapter.web.api;

import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.adapter.web.dto.WalletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal controller to handle wallet-related requests from other microservices.
 * These endpoints are secured by the ServiceAuthenticationFilter
 * and should NOT be publicly exposed via the Gateway.
 */
@Slf4j
@RestController
@RequestMapping("/internal/wallet")
@RequiredArgsConstructor
public class InternalWalletController {

    private final WalletUseCase walletUseCase;

    /**
     * Fetch the wallet ID for a given user ID.
     * Used by other microservices to get the wallet ID from the user ID.
     */
    @GetMapping("{userId}")
    public ResponseEntity<WalletResponse> getWalletIdByUserId(@PathVariable Long userId) {
        log.debug("Internal request: Getting wallet ID for user ID: {}", userId);

        WalletSuccess walletSuccess = walletUseCase.getWalletByUserId(userId);

        return walletSuccess != null
                ? ResponseEntity.ok(new WalletResponse(walletSuccess.getId(), walletSuccess.getCurrency(), walletSuccess.getAvailableBalance(), walletSuccess.getReservedBalance()))
                : ResponseEntity.notFound().build();
    }


    /**
     * Fetch the user ID for a given wallet ID.
     * Used by other microservices to get the user ID from the wallet ID.
     */
    @PostMapping("/debit/{userId}/{amount}")
    public void debitWallet(@PathVariable Long userId, @PathVariable BigDecimal amount) {
        walletUseCase.debit(userId, amount);
    }


    /**
     * Credit the wallet of a user by a specified amount.
     * Used by other microservices to add funds to a user's wallet.
     */
    @PostMapping("/credit/{userId}/{amount}")
    public void creditWallet(@PathVariable Long userId, @PathVariable BigDecimal amount) {
        walletUseCase.credit(userId, amount);
    }
}
