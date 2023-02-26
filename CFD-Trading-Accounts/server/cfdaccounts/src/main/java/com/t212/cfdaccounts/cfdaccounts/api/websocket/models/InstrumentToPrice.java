package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;


import java.math.BigDecimal;

public record InstrumentToPrice(String name, BigDecimal sellPrice, BigDecimal buyPrice, BigDecimal minQuantity,
                                String marketName, BigDecimal margin) {
}
