package com.brokerx.wallet_service.application.port.in.useCase;

import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.command.PositionSuccess;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;

import java.math.BigDecimal;
import java.util.List;

public interface WalletUseCase {

    /* Credit a specified amount to the user's wallet */
    void credit(Long userId, BigDecimal amount);

    /* Debit a specified amount from the user's wallet */
    void debit(Long userId, BigDecimal amount);

    /* Retrieve wallet details by user ID */
    WalletSuccess getWalletByUserId(Long userId);
    
    /* Retrieve transactions by user ID */
    List<TransactionSuccess> getTransactionsByUserId(Long userId);

    /* Retrieve positions by user ID */
    List<PositionSuccess> getPositionsByUserId(Long userId);
}
