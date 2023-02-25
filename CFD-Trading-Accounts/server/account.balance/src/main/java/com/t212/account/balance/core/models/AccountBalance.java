package com.t212.account.balance.core.models;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountBalance {
    public final BigDecimal balance;
    public final Date updatedAt;

    public AccountBalance(BigDecimal balance, Date updatedAt) {
        this.balance = balance;
        this.updatedAt = updatedAt;
    }
}
