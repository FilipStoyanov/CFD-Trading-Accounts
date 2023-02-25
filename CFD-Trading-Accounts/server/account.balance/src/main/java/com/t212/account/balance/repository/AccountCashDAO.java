package com.t212.account.balance.repository;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountCashDAO {
    public final long id;
    public final long userId;
    public final BigDecimal balance;
    public final Date createdAt;
    public final Date updatedAt;

    public AccountCashDAO(long id, long userId, BigDecimal balance, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
