package com.t212.tickers.producer.core.models;

import java.math.BigDecimal;

public record InstrumentUpdater(String ticker, BigDecimal buy, BigDecimal sell) {
}
