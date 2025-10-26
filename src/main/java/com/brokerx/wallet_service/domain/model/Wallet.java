package com.brokerx.wallet_service.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    private Long id;
    private Long userId;
    private String currency;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;


    /** Add funds to the wallet */
    public void credit(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
    }

    /** Debit funds from the wallet (used for order execution or withdrawal) */
    public void debit(BigDecimal amount) {
        if (this.availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance to debit");
        }
        this.availableBalance = this.availableBalance.subtract(amount);
    }

    /** RReserve funds for a pending order */
    public void reserve(BigDecimal amount) {
        if (this.availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance to reserve");
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.reservedBalance = this.reservedBalance.add(amount);
    }

    /** Release reserved funds (e.g. canceled order) */
    public void release(BigDecimal amount) {
        if (this.reservedBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient reserved balance to release");
        }
        this.reservedBalance = this.reservedBalance.subtract(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }

    /** Transfer reserved funds to a real debit (order execution) */
    public void commitReserved(BigDecimal amount) {
        if (this.reservedBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient reserved balance to commit");
        }
        this.reservedBalance = this.reservedBalance.subtract(amount);
        // Nothing to add to availableBalance, as this is a real outgoing
    }
}
