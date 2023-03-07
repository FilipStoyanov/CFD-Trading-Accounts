package com.t212.accounts.positions.repositories.mariadb;

import com.t212.accounts.positions.repositories.AccountPositionRepository;
import com.t212.accounts.positions.repositories.models.AccountPositionDAO;
import com.t212.accounts.positions.repositories.models.AccountPositionsWithPricesDAO;
import com.t212.accounts.positions.repositories.queries.PositionsQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MariaDBPositionRepository implements AccountPositionRepository {
    private final TransactionTemplate txTemplate;
    private final JdbcTemplate jdbc;

    public MariaDBPositionRepository(TransactionTemplate txTemplate, JdbcTemplate jdbc) {
        this.txTemplate = txTemplate;
        this.jdbc = jdbc;
    }

    @Override
    public List<AccountPositionDAO> getPositions(long userId) throws EmptyResultDataAccessException {
        return jdbc.query(PositionsQueries.GET_ALL_POSITIONS, (rs, rowNum) -> fromResultSetToPosition(rs), userId);
    }

    @Override
    public List<AccountPositionsWithPricesDAO> getPositionsWithCurrentPrices(long userId) throws EmptyResultDataAccessException {
        return jdbc.query(PositionsQueries.GET_ALL_OPEN_POSITIONS_WITH_CURRENT_PRICES, (rs, rowNum) -> fromResultSetToPositionWithPrices(rs), userId);
    }

    @Override
    public List<AccountPositionDAO> getClosePositions(long userId) {
        return jdbc.query(PositionsQueries.GET_CLOSE_POSITIONS, (rs, rowNum) -> fromResultSetToPosition(rs), userId);
    }

    @Override
    public AccountPositionDAO updatePosition(long userId, String ticker, String type) throws EmptyResultDataAccessException {
        jdbc.update(PositionsQueries.UPDATE_POSITION, userId, ticker, type);
        return getUpdatedPositionById(userId, ticker, type);
    }

    @Override
    public AccountPositionDAO getOpenPositionById(long userId, long instrumentId, String type) {
        return jdbc.queryForObject(PositionsQueries.GET_BY_INSTRUMENT_ID, (rs, rowNum) -> fromResultSetToPosition(rs), userId, instrumentId, type);
    }

    @Override
    public AccountPositionDAO getUpdatedPositionById(long userId, String ticker, String type) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(PositionsQueries.GET_UPDATED_POSITION_BY_ID, (rs, rowNum) -> fromResultSetToPosition(rs), userId, ticker, type);
    }

    @Override
    public AccountPositionDAO addPositionToUser(long userId, long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice) {
        jdbc.update(PositionsQueries.INSERT_POSITION, userId, instrumentId, quantity, type, buyPrice, sellPrice);
        return getOpenPositionById(userId, instrumentId, type);
    }

    private static AccountPositionDAO fromResultSetToPosition(ResultSet rs) throws SQLException {
        return new AccountPositionDAO(
                rs.getInt("user_id"),
                rs.getString("ticker"),
                rs.getBigDecimal("quantity"),
                rs.getString("type"),
                rs.getBigDecimal("buy_price"),
                rs.getBigDecimal("sell_price"),
                rs.getBoolean("is_closed"),
                rs.getDate("created_at"),
                rs.getDate("updated_at"),
                rs.getDate("deleted_at"),
                rs.getString("ticker_type")
        );
    }

    private static AccountPositionsWithPricesDAO fromResultSetToPositionWithPrices(ResultSet rs) throws SQLException {
        return new AccountPositionsWithPricesDAO(
                rs.getInt("user_id"),
                rs.getString("ticker"),
                rs.getBigDecimal("quantity"),
                rs.getString("type"),
                rs.getBigDecimal("buy_price"),
                rs.getBigDecimal("sell_price"),
                rs.getBoolean("is_closed"),
                rs.getBigDecimal("current_buy"),
                rs.getBigDecimal("current_sell"),
                rs.getDate("created_at"),
                rs.getDate("updated_at"),
                rs.getDate("deleted_at")
        );
    }
}
