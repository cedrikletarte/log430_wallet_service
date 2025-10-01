package com.brokerx.wallet_service.application.port.in.useCase;

import com.brokerx.wallet_service.application.port.in.command.WalletSuccess;
import com.brokerx.wallet_service.application.port.in.command.TransactionSuccess;

import java.math.BigDecimal;
import java.util.List;

public interface WalletUseCase {
    void credit(Long userId, BigDecimal amount);

    void debit(Long userId, BigDecimal amount);

    WalletSuccess getWalletByUserId(Long userId);
    
    List<TransactionSuccess> getTransactionsByUserId(Long userId);
}
