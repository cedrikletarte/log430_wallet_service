package com.brokerx.wallet_service.adapter.web.api;

import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.useCase.OrderWalletUseCase;
import com.brokerx.wallet_service.application.port.in.useCase.WalletUseCase;
import com.brokerx.wallet_service.adapter.web.dto.PositionResponse;
import com.brokerx.wallet_service.adapter.web.dto.WalletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    private final OrderWalletUseCase orderWalletUseCase;

    /* Fetch the wallet ID for a given user ID. */
    @GetMapping("{userId}")
    public ResponseEntity<WalletResponse> getWalletIdByUserId(@PathVariable Long userId) {
        log.debug("Internal request: Getting wallet ID for user ID: {}", userId);

        WalletSuccess walletSuccess = walletUseCase.getWalletByUserId(userId);

        return walletSuccess != null
                ? ResponseEntity.ok(new WalletResponse(walletSuccess.getId(), walletSuccess.getCurrency(), walletSuccess.getAvailableBalance(), walletSuccess.getReservedBalance()))
                : ResponseEntity.notFound().build();
    }

    /* Fetch the user ID for a given wallet ID. */
    @PostMapping("/debit/{userId}/{amount}")
    public void debitWallet(@PathVariable Long userId, @PathVariable BigDecimal amount) {
        walletUseCase.debit(userId, amount);
    }


    /* Credit the wallet of a user by a specified amount. */
    @PostMapping("/credit/{userId}/{amount}")
    public void creditWallet(@PathVariable Long userId, @PathVariable BigDecimal amount) {
        walletUseCase.credit(userId, amount);
    }

    /* Execute a buy order */
    @PostMapping("/execute/buy")
    public void executeBUY(@RequestBody PositionResponse entity) {
        orderWalletUseCase.executeOrder(entity.userId(), entity.symbol(), entity.side(), entity.quantity(), entity.price(), entity.orderId());
    }


    /* Execute a sell order */
    @PostMapping("/execute/sell")
    public void executeSELL(@RequestBody PositionResponse entity) {
        orderWalletUseCase.executeOrder(entity.userId(), entity.symbol(), entity.side(), entity.quantity(), entity.price(), entity.orderId());
    }

    /* Reserve a specific amount in the user's wallet for an order. */
    @PostMapping("/reserve/{userId}/{amount}/{orderId}")
    public void reserveAmount(@PathVariable Long userId, @PathVariable BigDecimal amount, @PathVariable Long orderId) {
        orderWalletUseCase.reserveFundsForWallet(userId, amount, orderId);
    }
    
}
