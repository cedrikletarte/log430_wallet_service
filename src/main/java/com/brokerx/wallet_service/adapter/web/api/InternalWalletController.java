package com.brokerx.wallet_service.adapter.web.api;

import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.adapter.web.dto.WalletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    private final WalletRepositoryPort walletRepositoryPort;

    /**
     * Fetch the wallet ID for a given user ID.
     * Used by other microservices to get the wallet ID from the user ID.
     */
    @GetMapping("{userId}")
    public ResponseEntity<WalletResponse> getWalletIdByUserId(@PathVariable Long userId) {
        log.debug("Internal request: Getting wallet ID for user ID: {}", userId);
        
        return walletRepositoryPort.findByUserId(userId)
                .map(wallet -> ResponseEntity.ok(new WalletResponse(wallet.getId(), wallet.getCurrency(), wallet.getBalance())))
                .orElse(ResponseEntity.notFound().build());
    }
}
