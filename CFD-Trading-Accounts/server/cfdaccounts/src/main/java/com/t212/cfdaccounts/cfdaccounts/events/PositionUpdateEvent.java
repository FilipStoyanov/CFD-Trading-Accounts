package com.t212.cfdaccounts.cfdaccounts.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public record PositionUpdateEvent(long userId, String ticker, String positionType, BigDecimal quantity,
                                  BigDecimal buyPrice, BigDecimal sellPrice,
                                  Long timestamp) implements Serializable {
    @JsonCreator
    public PositionUpdateEvent(
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
        var that = (PositionUpdateEvent) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.positionType, that.positionType) &&
                Objects.equals(this.quantity, that.quantity) &&
                Objects.equals(this.buyPrice, that.buyPrice) &&
                Objects.equals(this.sellPrice, that.sellPrice) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public String toString() {
        return "Message[" +
                "userId=" + userId + ", " +
                "ticker=" + ticker + ", " +
                "positionType=" + positionType + ", " +
                "quantity=" + quantity + ", " +
                "buyPrice=" + buyPrice + "," +
                "sellPrice=" + sellPrice + ',' +
                "timestamp=" + timestamp + "]";
    }
}
