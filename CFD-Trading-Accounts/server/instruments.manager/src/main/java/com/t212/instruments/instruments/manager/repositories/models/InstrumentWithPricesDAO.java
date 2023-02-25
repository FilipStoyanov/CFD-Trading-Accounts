package com.t212.instruments.instruments.manager.repositories.models;

import java.math.BigDecimal;
import java.sql.Date;

public class InstrumentWithPricesDAO {
    public final long id;
    public final String name;
    public final String ticker;
    public final String fullname;
    public final BigDecimal quantity;
    public final BigDecimal leverage;
    public final String marketName;
    public final BigDecimal buy;
    public final BigDecimal sell;
    public final Date createdAt;
    public final Date updatedAt;

    public InstrumentWithPricesDAO(long id, String name, String ticker, String fullname, BigDecimal quantity, BigDecimal leverage, String marketName, BigDecimal buy, BigDecimal sell, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.fullname = fullname;
        this.quantity = quantity;
        this.ticker = ticker;
        this.leverage = leverage;
        this.marketName = marketName;
        this.buy = buy;
        this.sell = sell;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
