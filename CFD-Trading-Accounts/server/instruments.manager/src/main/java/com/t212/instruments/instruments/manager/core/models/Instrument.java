package com.t212.instruments.instruments.manager.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public class Instrument {
    public final long id;
    public final String name;
    public final String ticker;
    public final String fullname;
    public final BigDecimal quantity;
    public final BigDecimal leverage;
    public final String marketName;
    public final Date createdAt;
    public final Date updatedAt;

    public Instrument(long id, String name, String ticker, String fullname, BigDecimal quantity, BigDecimal leverage, String marketName, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.ticker = ticker;
        this.fullname = fullname;
        this.quantity = quantity;
        this.leverage = leverage;
        this.marketName = marketName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
