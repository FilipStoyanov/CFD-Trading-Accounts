package com.t212.instruments.instruments.manager.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public record Instrument(long id, String name, String ticker, String fullname, String type, BigDecimal quantity, BigDecimal leverage,
                         String marketName, Date createdAt, Date updatedAt) {
}
