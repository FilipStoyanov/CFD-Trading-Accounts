package com.t212.instruments.instruments.manager.repositories.models;

import java.math.BigDecimal;
import java.sql.Date;

public record InstrumentDAO(long id, String name, String ticker, String fullname, BigDecimal quantity,
                            BigDecimal leverage, String marketName, Date createdAt, Date updatedAt) {
}
