package com.t212.instruments.instruments.manager.api.rest.models;

import java.math.BigDecimal;

public record InstrumentInput(String name, String fullname, BigDecimal quantity, BigDecimal leverage,
                              String marketName) {
}
