package com.t212.instruments.instruments.manager.core.models;

import java.math.BigDecimal;

public record InstrumentUpdater(String ticker, BigDecimal buy, BigDecimal sell) {
}