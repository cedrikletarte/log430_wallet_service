
package com.brokerx.wallet_service.application.port.in.useCase;

import java.math.BigDecimal;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;

public interface WalletWithIdempotencyUseCase {

    /* Credit wallet with idempotency check */
    TransactionSuccess creditWithIdempotency(Long userId, BigDecimal amount, String idempotencyKey);

    /* Debit wallet with idempotency check */
    TransactionSuccess debitWithIdempotency(Long userId, BigDecimal amount, String idempotencyKey);

    /* Check if a request is duplicate based on idempotency key */
    boolean isDuplicateRequest(String idempotencyKey, Long userId);

    /* Get cached response for an idempotency key */
    TransactionSuccess getCachedResponse(String idempotencyKey, Long userId);
}
