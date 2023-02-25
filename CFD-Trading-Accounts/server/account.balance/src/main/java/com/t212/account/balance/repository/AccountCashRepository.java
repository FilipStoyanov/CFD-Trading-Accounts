package com.t212.account.balance.repository;

import java.math.BigDecimal;

public interface AccountCashRepository {
    AccountCashDAO withdraw(BigDecimal amount, long ownerId);

    AccountCashDAO deposit(BigDecimal amount, long ownerId);

    AccountCashDAO getById(long ownerId);
}
