package com.t212.accounts.positions.api.rest.models;

import java.math.BigDecimal;

public record PositionInput(long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice,
                            BigDecimal sellPrice) {
}
