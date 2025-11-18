package com.brokerx.wallet_service.infrastructure.persistence.repository.position;

import com.brokerx.wallet_service.application.port.out.PositionRepositoryPort;
import com.brokerx.wallet_service.domain.model.Position;
import com.brokerx.wallet_service.infrastructure.persistence.mapper.PositionMapper;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class PositionRepositoryAdapter implements PositionRepositoryPort {

    private final SpringPositionRepository springPositionRepository;
    private final PositionMapper positionMapper;

    public PositionRepositoryAdapter(SpringPositionRepository springPositionRepository, PositionMapper positionMapper) {
        this.springPositionRepository = springPositionRepository;
        this.positionMapper = positionMapper;
    }

    /* Saves a Position to the repository */
    @Override
    public Position save(Position position) {
        return positionMapper.toDomain(springPositionRepository.save(positionMapper.toEntity(position)));
    }

    /* Finds a Position by its ID */
    @Override
    public Optional<Position> findById(Long id) {
        return springPositionRepository.findById(id).map(positionMapper::toDomain);
    }

    /* Finds a Position by wallet ID and symbol */
    @Override
    public Optional<Position> findByWalletIdAndSymbol(Long walletId, String symbol) {
        return springPositionRepository.findByWalletIdAndSymbol(walletId, symbol)
                .map(positionMapper::toDomain);
    }

    /* Finds all Positions by wallet ID */
    @Override
    public List<Position> findByWalletId(Long walletId) {
        return springPositionRepository.findByWalletId(walletId).stream()
                .map(positionMapper::toDomain)
                .toList();
    }

    /* Deletes a Position from the repository */
    @Override
    public void delete(Position position) {
        springPositionRepository.delete(positionMapper.toEntity(position));
    }

    /* Checks if a Position exists by wallet ID and symbol */
    @Override
    public boolean existsByWalletIdAndSymbol(Long walletId, String symbol) {
        return springPositionRepository.existsByWalletIdAndSymbol(walletId, symbol);
    }
}
