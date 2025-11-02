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

    @Override
    public Position save(Position position) {
        return positionMapper.toDomain(springPositionRepository.save(positionMapper.toEntity(position)));
    }

    @Override
    public Optional<Position> findById(Long id) {
        return springPositionRepository.findById(id).map(positionMapper::toDomain);
    }


    @Override
    public Optional<Position> findByWalletIdAndSymbol(Long walletId, String symbol) {
        return springPositionRepository.findByWalletIdAndSymbol(walletId, symbol)
                .map(positionMapper::toDomain);
    }

    @Override
    public List<Position> findByWalletId(Long walletId) {
        return springPositionRepository.findByWalletId(walletId).stream()
                .map(positionMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Position position) {
        springPositionRepository.delete(positionMapper.toEntity(position));
    }

    @Override
    public boolean existsByWalletIdAndSymbol(Long walletId, String symbol) {
        return springPositionRepository.existsByWalletIdAndSymbol(walletId, symbol);
    }
}
