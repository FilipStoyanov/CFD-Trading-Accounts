package com.t212.instruments.instruments.manager.repositories.queries;

public final class InstrumentQueries {
    public static final String INSERT_INSTRUMENT = "INSERT INTO instruments(name, fullname, min_quantity, leverage, market_name) " +
            "VALUES (?,?,?,?,?)";

    public static final String GET_INSTRUMENT_BY_ID = "SELECT i.id, i.name, i.ticker, i.fullname, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i " +
            "where i.id = ?";

    public static final String LIST_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i " +
            "limit ?,?";
    public static final String LIST_ALL_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i";

    public static final String DELETE_INSTRUMENT = "delete from instruments where id=?";

    public static final String GET_INSTRUMENT_BY_NAME = "SELECT i.id, i.name, i.ticker, i.fullname, i.min_quantity, i.leverage, i.created_at, i.updated_at " +
            "from instruments i " +
            "where i.name = ?";

    public static final String GET_ALL_INSTRUMENTS_WITH_INITIAL_PRICE = "SELECT i.id, i.name, i.ticker, i.fullname, i.market_name, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_prices i2p on i.id = i2p.instrument_id";

    public static final String GET_TOP_10_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, i.market_name, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_prices i2p on i.id = i2p.instrument_id " +
            "limit 10";
}
