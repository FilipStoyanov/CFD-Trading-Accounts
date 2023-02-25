package com.t212.cfdaccounts.cfdaccounts.repositories.models;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountCashDAO {
    public final Integer id;
    public final Integer userId;
    private BigDecimal balance;
    public final Date createdAt;
    public final Date updatedAt;

    public AccountCashDAO(Integer id, Integer userId, BigDecimal balance, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
