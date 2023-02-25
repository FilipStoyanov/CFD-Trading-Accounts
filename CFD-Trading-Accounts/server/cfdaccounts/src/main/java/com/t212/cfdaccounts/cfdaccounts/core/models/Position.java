package com.t212.cfdaccounts.cfdaccounts.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public class Position {
    public final long userId;
    public final String name;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final Boolean isClosed;
    public final Date createdAt;
    public final Date updatedAt;

    public Position(long userId, String name, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, Boolean isClosed, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
