package com.t212.account.balance.repository;

import java.math.BigDecimal;
import java.sql.Date;

public record AccountCashDAO(long id, long userId, BigDecimal balance, Date createdAt, Date updatedAt) {
}
