package com.brokerx.wallet_service.application.port.out;

import com.brokerx.wallet_service.domain.model.Position;

import java.util.List;
import java.util.Optional;

public interface PositionRepositoryPort {

    /* Save a position */
    Position save(Position position);

    /* Find a position by ID */
    Optional<Position> findById(Long id);

    /* Find a position by wallet ID and symbol */
    Optional<Position> findByWalletIdAndSymbol(Long walletId, String symbol);

    /* Find all positions for a wallet */
    List<Position> findByWalletId(Long walletId);

    /* Delete a position (when quantity reaches 0) */
    void delete(Position position);

    /* Check if a position exists */
    boolean existsByWalletIdAndSymbol(Long walletId, String symbol);
}
