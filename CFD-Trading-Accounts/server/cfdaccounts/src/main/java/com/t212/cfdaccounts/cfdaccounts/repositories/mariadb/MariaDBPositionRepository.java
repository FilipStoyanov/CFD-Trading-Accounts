package com.t212.cfdaccounts.cfdaccounts.repositories.mariadb;

import com.t212.cfdaccounts.cfdaccounts.repositories.AccountPositionRepository;
import com.t212.cfdaccounts.cfdaccounts.repositories.models.AccountPositionDAO;
import com.t212.cfdaccounts.cfdaccounts.repositories.queries.PositionsQueries;
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
    public AccountPositionDAO updatePosition(long userId, long instId) throws EmptyResultDataAccessException {
        jdbc.update(PositionsQueries.UPDATE_POSITION, userId, instId);
        return getUpdatedPositionById(userId, instId);
    }

    @Override
    public AccountPositionDAO getPositionById(long userId, long instrumentId) {
        return jdbc.queryForObject(PositionsQueries.GET_BY_INSTRUMENT_ID, (rs, rowNum) -> fromResultSetToPosition(rs), userId, instrumentId);
    }

    @Override
    public AccountPositionDAO getUpdatedPositionById(long userId, long instrumentId) {
        return jdbc.queryForObject(PositionsQueries.GET_UPDATED_POSITION_BY_ID, (rs, rowNum) -> fromResultSetToPosition(rs), userId, instrumentId);
    }

    @Override
    public AccountPositionDAO addPositionToUser(long userId, long instrumentId, BigDecimal quantity, String type, BigDecimal buyPrice, BigDecimal sellPrice) {
        jdbc.update(PositionsQueries.INSERT_POSITION, userId, instrumentId, quantity, type, buyPrice, sellPrice);
        return getPositionById(userId, instrumentId);
    }


    private static AccountPositionDAO fromResultSetToPosition(ResultSet rs) throws SQLException {
        return new AccountPositionDAO(
                rs.getInt("user_id"),
                rs.getString("ticker"),
                rs.getBigDecimal("quantity"),
                rs.getString("type"),
                rs.getBigDecimal("buy_price"),
                rs.getBigDecimal("sell_price"),
                rs.getDate("created_at"),
                rs.getDate("updated_at")
        );
    }
}
