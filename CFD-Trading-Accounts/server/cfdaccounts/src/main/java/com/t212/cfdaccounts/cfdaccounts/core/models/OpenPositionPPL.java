package com.t212.cfdaccounts.cfdaccounts.core.models;

import java.math.BigDecimal;

public class OpenPositionPPL {
    public final String ticker;
    public final BigDecimal quantity;
    public final String type;
    public final BigDecimal price;
    public final BigDecimal currentPrice;
    public final BigDecimal margin;
    public final BigDecimal result;

    public OpenPositionPPL(String ticker, BigDecimal quantity, String type, BigDecimal price, BigDecimal currentPrice, BigDecimal margin, BigDecimal result) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.currentPrice = currentPrice;
        this.margin = margin;
        this.result = result;
    }
}
