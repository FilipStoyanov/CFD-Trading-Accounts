package com.t212.cfdaccounts.cfdaccounts.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.t212.cfdaccounts.cfdaccounts.events.models.PositionsUpdateType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class PositionsUpdaterEvent implements Serializable {
    public final long userId;
    public final String ticker;
    public final String positionType;
    public final BigDecimal quantity;
    public final BigDecimal buyPrice;
    public final BigDecimal sellPrice;
    public final PositionsUpdateType type;
    public final Long timestamp;

    @JsonCreator
    public PositionsUpdaterEvent(
            @JsonProperty("userId") long userId,
            @JsonProperty("ticker") String ticker,
            @JsonProperty("positionType") String positionType,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("buyPrice") BigDecimal buyPrice,
            @JsonProperty("sellPrice") BigDecimal sellPrice,
            @JsonProperty("type") PositionsUpdateType type,
            @JsonProperty("timestamp") Long timestamp) {
        this.userId = userId;
        this.ticker = ticker;
        this.positionType = positionType;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.type = type;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PositionsUpdaterEvent) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.positionType, that.positionType) &&
                Objects.equals(this.quantity, that.quantity) &&
                Objects.equals(this.buyPrice, that.buyPrice) &&
                Objects.equals(this.sellPrice, that.sellPrice) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.ticker, this.positionType, this.quantity, this.buyPrice, this.sellPrice, this.type, this.timestamp);
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
                "type=" + type + "," +
                "timestamp=" + timestamp + "]";
    }
}
