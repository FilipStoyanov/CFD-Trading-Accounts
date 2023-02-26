package com.t212.account.balance.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public record AccountBalance(BigDecimal balance, Date updatedAt) {
}
