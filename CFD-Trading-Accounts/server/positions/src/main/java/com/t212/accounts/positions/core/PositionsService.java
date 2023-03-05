package com.t212.accounts.positions.core;

import com.t212.accounts.positions.api.rest.models.OpenPositionOutput;
import com.t212.accounts.positions.core.models.Position;
import com.t212.accounts.positions.core.models.PositionWithPrices;
import com.t212.accounts.positions.repositories.AccountPositionRepository;
import com.t212.accounts.positions.repositories.models.AccountPositionDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionsService {
    public final AccountPositionRepository positionRepository;

    public PositionsService(AccountPositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public List<OpenPositionOutput> getOpenPositionsById(long userId) throws EmptyResultDataAccessException {
        return positionRepository.getPositions(userId).stream().map(current -> Mappers.fromResultSetToOpenPosition(current)).collect(Collectors.toList());
    }

    public List<String> getAllTypesOfPositions(long userId) throws EmptyResultDataAccessException {
        return positionRepository.getAllTypesOfPositions(userId);
    }

    public List<PositionWithPrices> getOpenPositionsWithPricesById(long userId) throws EmptyResultDataAccessException {
        return positionRepository.getPositionsWithCurrentPrices(userId).stream().map(current -> Mappers.fromResultSetToPositionWithPrices(current)).collect(Collectors.toList());
    }

    public List<Position> getClosePositionsById(long userId) throws EmptyResultDataAccessException {
        return positionRepository.getClosePositions(userId).stream().map(current -> Mappers.fromResultSetToPosition(current)).collect(Collectors.toList());
    }

    public Position addPosition(long userId, long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice) {
        AccountPositionDAO position = positionRepository.addPositionToUser(userId, instrumentId, quantity, type, buyPrice, sellPrice);
        return Mappers.fromResultSetToPosition(position);
    }

    public Position updatePosition(long userId, long instrumentId, String type) {
        AccountPositionDAO position = positionRepository.updatePosition(userId, instrumentId, type);
        return Mappers.fromResultSetToPosition(position);
    }
}
