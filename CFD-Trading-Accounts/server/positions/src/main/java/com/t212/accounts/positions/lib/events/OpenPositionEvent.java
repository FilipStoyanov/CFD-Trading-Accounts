package com.t212.accounts.positions.lib.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class OpenPositionEvent implements Serializable {
    public final long userId;
    public final String ticker;
    public final String positionType;
    public final BigDecimal quantity;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final Long timestamp;

    @JsonCreator
    public OpenPositionEvent(
            @JsonProperty("userId") long userId,
            @JsonProperty("ticker") String ticker,
            @JsonProperty("positionType") String positionType,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("buyPrice") BigDecimal buyPrice,
            @JsonProperty("sellPrice") BigDecimal sellPrice,
            @JsonProperty("timestamp") Long timestamp) {
        this.userId = userId;
        this.ticker = ticker;
        this.positionType = positionType;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OpenPositionEvent) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.positionType, that.positionType) &&
                Objects.equals(this.quantity, that.quantity) &&
                Objects.equals(this.buyPrice, that.buyPrice) &&
                Objects.equals(this.sellPrice, that.sellPrice) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.ticker, this.positionType, this.quantity, this.buyPrice, this.sellPrice, this.timestamp);
    }
}
