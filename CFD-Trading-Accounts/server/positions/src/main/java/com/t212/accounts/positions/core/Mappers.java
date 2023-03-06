package com.t212.accounts.positions.core;

import com.t212.accounts.positions.api.rest.models.OpenPositionOutput;
import com.t212.accounts.positions.core.models.Position;
import com.t212.accounts.positions.core.models.PositionWithPrices;
import com.t212.accounts.positions.repositories.models.AccountPositionDAO;
import com.t212.accounts.positions.repositories.models.AccountPositionsWithPricesDAO;

public class Mappers {
    public static Position fromResultSetToPosition(AccountPositionDAO position) {
        return new Position(position.ticker, position.quantity, position.type, position.buyPrice, position.sellPrice,
                position.isClosed, position.createdAt, position.updatedAt, position.tickerType);
    }

    public static PositionWithPrices fromResultSetToPositionWithPrices(AccountPositionsWithPricesDAO position) {
        return new PositionWithPrices(position.ticker, position.quantity, position.type, position.buyPrice, position.sellPrice,
                position.currentBuy, position.currentSell, position.isClosed, position.createdAt, position.updatedAt);
    }

    public static OpenPositionOutput fromResultSetToOpenPosition(AccountPositionDAO position) {
        return new OpenPositionOutput(position.ticker, position.quantity, position.type, position.buyPrice, position.sellPrice, position.createdAt, position.updatedAt, position.tickerType);
    }
}
