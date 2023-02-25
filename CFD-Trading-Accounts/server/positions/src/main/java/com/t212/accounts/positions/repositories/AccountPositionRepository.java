package com.t212.accounts.positions.repositories;

import com.t212.accounts.positions.repositories.models.AccountPositionDAO;
import com.t212.accounts.positions.repositories.models.AccountPositionsWithPricesDAO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountPositionRepository {
    List<AccountPositionDAO> getPositions(long userId);
    List<AccountPositionsWithPricesDAO> getPositionsWithCurrentPrices(long userId);

    List<AccountPositionDAO> getClosePositions(long userId);

    AccountPositionDAO updatePosition(long userId, long instrumentId, String type);

    AccountPositionDAO getOpenPositionById(long userId, long instrumentId, String type);

    AccountPositionDAO getUpdatedPositionById(long userId, long instrumentId, String type);

    AccountPositionDAO addPositionToUser(long userId, long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice);
}
