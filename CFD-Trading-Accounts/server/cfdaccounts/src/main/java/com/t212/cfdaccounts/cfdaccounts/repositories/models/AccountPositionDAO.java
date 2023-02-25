package com.t212.cfdaccounts.cfdaccounts.repositories.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountPositionDAO {
    public final long userId;
    public final String ticker;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final Date createdAt;
    public final Date updatedAt;

    @JsonCreator
    public AccountPositionDAO(
            @JsonProperty("userId") long userId,
            @JsonProperty("ticker") String ticker,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("type") String type,
            @JsonProperty("buyPrice") BigDecimal buyPrice,
            @JsonProperty("sellPrice") BigDecimal sellPrice,
            @JsonProperty("createdAt") Date createdAt,
            @JsonProperty("updatedAt") Date updatedAt) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AccountPositionDAO(long userId, String ticker, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice, Date date) {
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.createdAt = date;
        this.updatedAt = date;
    }
}
