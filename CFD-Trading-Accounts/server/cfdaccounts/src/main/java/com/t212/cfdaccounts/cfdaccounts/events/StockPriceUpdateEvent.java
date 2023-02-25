package com.t212.cfdaccounts.cfdaccounts.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class StockPriceUpdateEvent implements Serializable {
    public final String ticker;
    public final BigDecimal bid;
    public final BigDecimal ask;
    public final Long timestamp;

    @JsonCreator
    public StockPriceUpdateEvent(
            @JsonProperty("ticker") String ticker,
            @JsonProperty("bid") BigDecimal bid,
            @JsonProperty("ask") BigDecimal ask,
            @JsonProperty("timestamp") Long timestamp) {
        this.ticker = ticker;
        this.bid = bid;
        this.ask = ask;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StockPriceUpdateEvent) obj;
        return Objects.equals(this.ticker, that.ticker) &&
                Objects.equals(this.bid, that.bid) &&
                Objects.equals(this.ask, that.ask) &&
                Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ticker, this.bid, this.ask, this.timestamp);
    }

    @Override
    public String toString() {
        return "Message[" +
                "ticker=" + ticker + ", " +
                "bid=" + bid + ", " +
                "ask=" + ask + "," +
                "timestamp=" + timestamp + "]";
    }
}
