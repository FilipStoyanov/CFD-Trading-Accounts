package com.t212.cfdaccounts.cfdaccounts.repositories;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountCashDAO;
import java.math.BigDecimal;

public interface AccountCashRepository {
    AccountCashDAO withdraw(BigDecimal amount, long ownerId);

    AccountCashDAO deposit(BigDecimal amount, long ownerId);

    AccountCashDAO getById(long ownerId);
}
