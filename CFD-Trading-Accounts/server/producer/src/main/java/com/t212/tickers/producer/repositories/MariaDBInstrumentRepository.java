package com.t212.tickers.producer.repositories;

import com.t212.tickers.producer.core.models.InstrumentUpdater;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MariaDBInstrumentRepository implements InstrumentRepository {
    private final JdbcTemplate jdbcTemplate;

    public MariaDBInstrumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void batchUpdate(List<InstrumentUpdater> instruments) {
        String sql = "UPDATE instrument_prices SET buy = ?, sell = ? WHERE ticker = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBigDecimal(1, instruments.get(i).buy());
                ps.setBigDecimal(2, instruments.get(i).sell());
                ps.setString(3, instruments.get(i).ticker());
            }

            @Override
            public int getBatchSize() {
                return instruments.size();
            }
        });
    }
}
