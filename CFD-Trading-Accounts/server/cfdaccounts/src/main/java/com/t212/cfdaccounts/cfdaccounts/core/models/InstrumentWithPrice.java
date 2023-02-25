package com.t212.cfdaccounts.cfdaccounts.core.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class InstrumentWithPrice  {
    public final long id;
    public final String name;
    public final String ticker;
    public final String fullname;
    public final BigDecimal quantity;
    public final BigDecimal leverage;
    public final String marketName;
    private  BigDecimal buy;
    private  BigDecimal sell;
    public final Date createdAt;
    public final Date updatedAt;

    public InstrumentWithPrice(long id, String name, String ticker, String fullname, BigDecimal quantity, BigDecimal leverage, String marketName, BigDecimal buy, BigDecimal sell, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.ticker = ticker;
        this.fullname = fullname;
        this.quantity = quantity;
        this.leverage = leverage;
        this.marketName = marketName;
        this.buy = buy;
        this.sell = sell;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }
}
