package com.t212.instruments.instruments.manager.api.rest.models;

import java.math.BigDecimal;

public class InstrumentInput {
    public final String name;
    public final String fullname;
    public final BigDecimal quantity;
    public final BigDecimal leverage;
    public final String marketName;

    public InstrumentInput(String name, String fullname, BigDecimal quantity, BigDecimal leverage, String marketName) {
        this.name = name;
        this.fullname = fullname;
        this.quantity = quantity;
        this.leverage = leverage;
        this.marketName = marketName;
    }
}
