package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;

public class PositionInput {
    public final long instrumentId;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;

    public PositionInput(long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice) {
        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
}
