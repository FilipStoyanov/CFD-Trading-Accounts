package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;

public class PositionUpdateInput {
    public final BigDecimal quantity;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final String positionType;

    public PositionUpdateInput(BigDecimal quantity, BigDecimal buyPrice, BigDecimal sellPrice, String positionType) {
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.positionType = positionType;
    }
}
