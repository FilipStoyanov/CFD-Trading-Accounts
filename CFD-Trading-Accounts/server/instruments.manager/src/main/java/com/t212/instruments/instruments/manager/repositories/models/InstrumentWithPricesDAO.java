package com.t212.instruments.instruments.manager.repositories.models;

import java.math.BigDecimal;
import java.sql.Date;

public record InstrumentWithPricesDAO(long id, String name, String ticker, String fullname, BigDecimal quantity,
                                      BigDecimal leverage, String marketName, BigDecimal buy, BigDecimal sell,
                                      Date createdAt, Date updatedAt) {
}
