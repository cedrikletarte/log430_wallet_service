package com.brokerx.wallet_service.application.service;

import com.brokerx.wallet_service.application.port.out.PositionRepositoryPort;
import com.brokerx.wallet_service.application.port.out.TransactionRepositoryPort;
import com.brokerx.wallet_service.application.port.out.WalletRepositoryPort;
import com.brokerx.wallet_service.domain.model.Position;
import com.brokerx.wallet_service.domain.model.Transaction;
import com.brokerx.wallet_service.domain.model.TransactionStatus;
import com.brokerx.wallet_service.domain.model.TransactionType;
import com.brokerx.wallet_service.domain.model.Wallet;
import com.brokerx.wallet_service.domain.service.WalletValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/* Service responsible for compensating wallet when orders fail */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCompensationService {

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final PositionRepositoryPort positionRepositoryPort;

    /**
     * Compensate a failed order by:
     * - For BUY orders: Restore cash (move from reserved to available)
     * - For SELL orders: Restore shares (increase available quantity in position)
     * - Create compensation transaction for audit trail
     */
    @Transactional
    public void compensateFailedOrder(
            Long walletId,
            Long orderId,
            String side,
            String stockSymbol,
            Integer quantity,
            BigDecimal totalAmount,
            String reason
    ) {
        log.info("🔄 Starting wallet compensation for order {} - walletId={}, side={}, reason={}",
                orderId, walletId, side, reason);

        Wallet wallet = walletRepositoryPort.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for compensation: " + walletId));

        if ("BUY".equalsIgnoreCase(side)) {
            compensateBuyOrder(wallet, totalAmount, orderId, stockSymbol, quantity, reason);
        } else if ("SELL".equalsIgnoreCase(side)) {
            compensateSellOrder(wallet, stockSymbol, quantity, orderId, reason);
        } else {
            throw new IllegalArgumentException("Invalid order side for compensation: " + side);
        }

        log.info("✅ Wallet compensation completed for order {}", orderId);
    }

    /* Compensate BUY order: Restore reserved cash to available balance */
    private void compensateBuyOrder(
            Wallet wallet,
            BigDecimal amount,
            Long orderId,
            String stockSymbol,
            Integer quantity,
            String reason
    ) {
        log.info("💰 Compensating BUY order: Restoring ${} to available balance", amount);

        // Move funds from reserved back to available
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));

        WalletValidator.validateUpdate(wallet);
        walletRepositoryPort.save(wallet);

        // Create compensation transaction for audit trail
        Transaction compensationTx = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT) // Credit because we're giving money back
                .status(TransactionStatus.SETTLED)
                .amount(amount)
                .createdAt(Instant.now())
                .settledAt(Instant.now())
                .isSettled(true)
                .build();

        transactionRepositoryPort.save(compensationTx);

        log.info("📝 BUY order compensation: reserved ${} → available ${}, compensation tx created",
                wallet.getReservedBalance(), wallet.getAvailableBalance());
    }

    /* Compensate SELL order: Restore shares to position */
    private void compensateSellOrder(
            Wallet wallet,
            String stockSymbol,
            Integer quantity,
            Long orderId,
            String reason
    ) {
        log.info("📈 Compensating SELL order: Restoring {} shares of {} to position",
                quantity, stockSymbol);

        // Find the position
        Position position = positionRepositoryPort.findByWalletIdAndSymbol(wallet.getId(), stockSymbol)
                .orElseThrow(() -> new RuntimeException(
                        "Position not found for compensation: walletId=" + wallet.getId() + ", symbol=" + stockSymbol));

        // Restore shares (increase quantity)
        position.setQuantity(position.getQuantity() + quantity);

        positionRepositoryPort.save(position);

        log.info("📝 SELL order compensation: {} shares restored, new quantity: {}",
                quantity, position.getQuantity());
    }
}
