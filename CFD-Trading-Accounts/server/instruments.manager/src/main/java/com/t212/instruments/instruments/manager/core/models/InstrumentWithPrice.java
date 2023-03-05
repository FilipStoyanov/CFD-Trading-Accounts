package com.t212.instruments.instruments.manager.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public record InstrumentWithPrice(long id, String name, String ticker, String fullname, BigDecimal quantity,
                                  BigDecimal leverage, String type, String marketName, BigDecimal buy, BigDecimal sell,
                                  Date createdAt, Date updatedAt) {
}
