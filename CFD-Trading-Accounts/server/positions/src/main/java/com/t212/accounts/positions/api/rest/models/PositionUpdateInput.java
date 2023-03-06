package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;

public record PositionUpdateInput(String ticker, BigDecimal quantity, BigDecimal buyPrice, BigDecimal sellPrice, String positionType) {
}
