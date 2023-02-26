package com.t212.cfdaccounts.cfdaccounts.core.models;

import java.math.BigDecimal;

public record OpenPositionPPL(String ticker, BigDecimal quantity, String type, BigDecimal price,
                              BigDecimal currentPrice, BigDecimal margin, BigDecimal result) {
}
