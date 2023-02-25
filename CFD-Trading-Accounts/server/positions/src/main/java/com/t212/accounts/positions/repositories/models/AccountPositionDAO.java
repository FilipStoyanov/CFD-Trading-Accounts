package com.t212.accounts.positions.repositories.models;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountPositionDAO {
    public final long userId;
    public final String ticker;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final Boolean isClosed;
    public final Date createdAt;
    public final Date updatedAt;
    public Date deletedAt;


    public AccountPositionDAO(long userId, String ticker, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, Date date) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.createdAt = date;
        this.updatedAt = date;
        this.isClosed = false;
        this.deletedAt = null;
    }

    public AccountPositionDAO(long userId, String ticker, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, Boolean isClosed, Date createdAt, Date updatedAt, Date deletedAt) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
