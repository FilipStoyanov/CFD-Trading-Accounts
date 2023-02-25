package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;
import java.sql.Date;

public class OpenPositionOutput {
    public final long userId;
    public final String ticker;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final Date createdAt;
    public final Date updatedAt;

    public OpenPositionOutput(long userId, String ticker, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
