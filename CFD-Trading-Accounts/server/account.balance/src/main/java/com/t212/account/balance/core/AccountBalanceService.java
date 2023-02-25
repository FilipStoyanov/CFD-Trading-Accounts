package com.t212.account.balance.core;

import com.t212.account.balance.core.models.AccountBalance;
import com.t212.account.balance.repository.AccountCashDAO;
import com.t212.account.balance.repository.MariaDBAccountCashRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
public class AccountBalanceService {
    private final MariaDBAccountCashRepository accountCashRepository;
    private final TransactionTemplate transactionTemplate;

    public AccountBalanceService(MariaDBAccountCashRepository accountCashRepository, TransactionTemplate transactionTemplate) {
        this.accountCashRepository = accountCashRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public AccountBalance getBalanceByUserId(long userId) {
        return Mappers.fromResultSetToAccountBalance(accountCashRepository.getById(userId));
    }

    public AccountBalance withdraw(long userId, BigDecimal amount) {
        final AccountCashDAO[] balance = new AccountCashDAO[1];
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                AccountCashDAO currentBalance = accountCashRepository.getById(userId);
                if (currentBalance.balance.compareTo(amount) >= 0) {
                    balance[0] = accountCashRepository.withdraw(amount, userId);
                }
            }
        });
        return getBalanceByUserId(userId);
    }

    public AccountBalance deposit(long userId, BigDecimal amount) {
        AccountCashDAO balance = accountCashRepository.deposit(amount, userId);
        return Mappers.fromResultSetToAccountBalance(balance);
    }
}
