package com.t212.instruments.instruments.manager.repositories.mariadb;

import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import com.t212.instruments.instruments.manager.repositories.InstrumentRepository;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentWithPricesDAO;
import com.t212.instruments.instruments.manager.repositories.queries.InstrumentQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class MariaDBInstrumentRepository implements InstrumentRepository {

    private final TransactionTemplate txTemplate;
    private final JdbcTemplate jdbc;

    public MariaDBInstrumentRepository(TransactionTemplate txTemplate, JdbcTemplate jdbc) {
        this.txTemplate = txTemplate;
        this.jdbc = jdbc;
    }

    @Override
    public InstrumentDAO addInstrument(String name, String fullName, String ticker, BigDecimal minQuantity, BigDecimal leverage, long typeId, String marketName) {
        return txTemplate.execute(status -> {
            try {
                KeyHolder keyholder = new GeneratedKeyHolder();
                jdbc.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(InstrumentQueries.INSERT_INSTRUMENT, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setString(2, fullName);
                    ps.setString(3, ticker);
                    ps.setBigDecimal(4, minQuantity);
                    ps.setBigDecimal(5, leverage);
                    ps.setLong(6, typeId);
                    ps.setString(7, marketName);
                    return ps;
                }, keyholder);
                int id = Objects.requireNonNull(keyholder.getKey()).intValue();
                return getInstrument(id);
            } catch (EmptyResultDataAccessException e) {
                return null;
            }
        });
    }

    @Override
    public InstrumentDAO getInstrument(long id) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(InstrumentQueries.GET_INSTRUMENT_BY_ID, (rs, rowNum) -> fromResultSetToInstrument(rs), id);
    }

    @Override
    public List<InstrumentDAO> listInstruments(Integer page, Integer pageSize) {
        return jdbc.query(InstrumentQueries.LIST_INSTRUMENTS, (rs, rowNum) -> fromResultSetToInstrument(rs), page * pageSize, pageSize);
    }

    @Override
    public boolean deleteInstrument(long id) throws DataAccessException {
        return jdbc.update(InstrumentQueries.DELETE_INSTRUMENT, id) > 0;
    }

    @Override
    public InstrumentDAO getInstrumentByName(String name) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(InstrumentQueries.GET_INSTRUMENT_BY_NAME, (rs, rowNum) -> fromResultSetToInstrument(rs), name);
    }

    @Override
    public List<InstrumentDAO> listAllInstruments() throws EmptyResultDataAccessException {
        return jdbc.query(InstrumentQueries.LIST_ALL_INSTRUMENTS, (rs, rowNum) -> fromResultSetToInstrument(rs));
    }

    @Override
    public List<InstrumentWithPricesDAO> getAllInstrumentsWithInitialPrice() throws EmptyResultDataAccessException {
        return jdbc.query(InstrumentQueries.GET_ALL_INSTRUMENTS_WITH_INITIAL_PRICE, (rs, rowNum) -> fromResultSetToInstrumentWithPrices(rs));
    }

    @Override
    public InstrumentWithPricesDAO getInstrumentWithInitialPrice(String ticker) {
        return jdbc.queryForObject(InstrumentQueries.GET_INSTRUMENT_WITH_INITIAL_PRICE, (rs, rowNum) -> fromResultSetToInstrumentWithPrices(rs), ticker);
    }

    @Override
    public List<InstrumentWithPricesDAO> getTop10Instruments() throws EmptyResultDataAccessException {
        return jdbc.query(InstrumentQueries.GET_TOP_10_INSTRUMENTS, (rs, rowNum) -> fromResultSetToInstrumentWithPrices(rs));
    }

    @Override
    public List<InstrumentWithPricesDAO> getPaginatedInstrumentsWithPrices(Integer page, Integer pageSize) {
        return jdbc.query(InstrumentQueries.GET_INSTRUMENTS_WITH_PAGINATION, (rs, rowNum) -> fromResultSetToInstrumentWithPrices(rs), page*pageSize, pageSize);
    }

    @Override
    public long getTypeId(String type) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(InstrumentQueries.GET_TYPE_ID, (rs, rowNum) -> rs.getLong("id"), type);
    }

    private static InstrumentDAO fromResultSetToInstrument(ResultSet rs) throws SQLException {
        return new InstrumentDAO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("ticker"),
                rs.getString("fullname"),
                rs.getString("type"),
                rs.getBigDecimal("min_quantity"),
                rs.getBigDecimal("leverage"),
                rs.getString("market_name"),
                rs.getDate("created_at"),
                rs.getDate("updated_at")
        );
    }

    private static InstrumentWithPricesDAO fromResultSetToInstrumentWithPrices(ResultSet rs) throws SQLException {
        return new InstrumentWithPricesDAO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("ticker"),
                rs.getString("fullname"),
                rs.getBigDecimal("min_quantity"),
                rs.getBigDecimal("leverage"),
                rs.getString("type"),
                rs.getString("market_name"),
                rs.getBigDecimal("buy"),
                rs.getBigDecimal("sell"),
                rs.getDate("created_at"),
                rs.getDate("updated_at")
        );
    }
}

