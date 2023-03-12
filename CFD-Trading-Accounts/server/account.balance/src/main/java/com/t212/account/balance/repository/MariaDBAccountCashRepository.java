package com.t212.account.balance.repository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MariaDBAccountCashRepository implements AccountCashRepository {

    private final JdbcTemplate jdbc;
    private final TransactionTemplate txTemplate;

    public MariaDBAccountCashRepository(JdbcTemplate jdbc, TransactionTemplate txTemplate) {
        this.jdbc = jdbc;
        this.txTemplate = txTemplate;
    }

    @Override
    public AccountCashDAO withdraw(BigDecimal amount, long ownerId) throws DataAccessException {
        return txTemplate.execute(status -> {
            jdbc.update(AccountCashQueries.WITHDRAW_AMOUNT, amount, ownerId);
            return getById(ownerId);
        });
    }

    @Override
    public AccountCashDAO deposit(BigDecimal amount, long ownerId) throws DataAccessException {
        return txTemplate.execute(status -> {
            jdbc.update(AccountCashQueries.DEPOSIT_AMOUNT, amount, ownerId);
            return getById(ownerId);
        });
    }

    @Override
    public AccountCashDAO getById(long ownerId) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(AccountCashQueries.GET_ACCOUNT_CASH, (rs, rowNum) -> fromResultSetToAccountCash(rs), ownerId);
    }

    private static AccountCashDAO fromResultSetToAccountCash(ResultSet rs) throws SQLException {
        return new AccountCashDAO(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getBigDecimal("balance"),
                rs.getDate("created_at"),
                rs.getDate("updated_at")
        );
    }
}
