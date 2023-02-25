package com.t212.cfdaccounts.cfdaccounts.repositories;

import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import java.math.BigDecimal;
import java.util.List;

public interface AccountPositionRepository {
    List<AccountPositionDAO> getPositions(long userId);

    AccountPositionDAO updatePosition(long userId, long instrumentId);

    AccountPositionDAO getPositionById(long userId, long instrumentId);

    AccountPositionDAO getUpdatedPositionById(long userId, long instrumentId);

    AccountPositionDAO addPositionToUser(long userId, long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice);
}
