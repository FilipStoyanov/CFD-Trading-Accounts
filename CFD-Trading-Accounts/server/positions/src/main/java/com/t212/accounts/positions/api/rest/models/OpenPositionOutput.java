package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;
import java.sql.Date;

public record OpenPositionOutput(String ticker, BigDecimal quantity, String type,
                                 BigDecimal buyPrice,
                                 BigDecimal sellPrice, Date createdAt, Date updatedAt) {
}
