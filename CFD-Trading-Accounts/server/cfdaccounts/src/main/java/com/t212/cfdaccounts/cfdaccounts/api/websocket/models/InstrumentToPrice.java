package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;


import java.math.BigDecimal;

public class InstrumentToPrice {
    public final String name;
    public final BigDecimal sellPrice;
    public final BigDecimal buyPrice;
    public final BigDecimal minQuantity;
    public final String marketName;
    public final BigDecimal margin;

    public InstrumentToPrice(String name, BigDecimal sellPrice, BigDecimal buyPrice, BigDecimal minQuantity, String marketName, BigDecimal margin) {
        this.name = name;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.minQuantity = minQuantity;
        this.marketName = marketName;
        this.margin = margin;
    }
}
