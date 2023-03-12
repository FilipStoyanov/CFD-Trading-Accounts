package com.t212.cfdaccounts.cfdaccounts.serviceclient.models;

import java.math.BigDecimal;
import java.sql.Date;

public record AccountBalance(BigDecimal balance, Date updatedAt) {
}
