package com.t212.account.balance.core;

import com.t212.account.balance.core.models.AccountBalance;
import com.t212.account.balance.repository.AccountCashDAO;

public class Mappers {
    public static AccountBalance fromResultSetToAccountBalance(AccountCashDAO balance) {
        return new AccountBalance(balance.balance(), balance.updatedAt());
    }

}
