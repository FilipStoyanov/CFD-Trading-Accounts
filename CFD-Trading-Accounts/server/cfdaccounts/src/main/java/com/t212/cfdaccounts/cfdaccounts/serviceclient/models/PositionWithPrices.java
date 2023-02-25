package com.t212.cfdaccounts.cfdaccounts.serviceclient.models;

import java.math.BigDecimal;
import java.sql.Date;

public class PositionWithPrices {
    public final String ticker;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final BigDecimal currentBuy;
    public final BigDecimal currentSell;
    public final Boolean isClosed;
    public final Date createdAt;
    public final Date updatedAt;

    public PositionWithPrices(String name, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, BigDecimal currentBuy, BigDecimal currentSell, Boolean isClosed, Date createdAt, Date updatedAt) {
        this.ticker = name;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.currentBuy = currentBuy;
        this.currentSell = currentSell;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
