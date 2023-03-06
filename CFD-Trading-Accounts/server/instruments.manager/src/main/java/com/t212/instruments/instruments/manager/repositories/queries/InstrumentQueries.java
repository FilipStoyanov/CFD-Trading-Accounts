package com.t212.instruments.instruments.manager.repositories.queries;

public final class InstrumentQueries {
    public static final String INSERT_INSTRUMENT = "INSERT INTO instruments(name, fullname, ticker, min_quantity, leverage, type_id, market_name) " +
            "VALUES (?,?,?,?,?,?,?)";

    public static final String GET_INSTRUMENT_BY_ID = "SELECT i.id, i.name, i.ticker, i.fullname, t.name as type, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "where i.id = ?";

    public static final String LIST_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, t.name as type, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "limit ?,?";
    public static final String LIST_ALL_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, t.name as type, i.min_quantity, i.leverage, i.market_name, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id";
    public static final String GET_INSTRUMENTS_WITH_PAGINATION = "SELECT i.id, i.name, i.ticker, i.fullname, i.market_name, t.name as type, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "left join instrument_prices i2p on i.ticker = i2p.ticker " +
            "limit ?,?";
    public static final String DELETE_INSTRUMENT = "delete from instruments where id=?";

    public static final String GET_INSTRUMENT_BY_NAME = "SELECT i.id, i.name, i.ticker, i.fullname, t.name as type, i.min_quantity, i.leverage, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "where i.name = ?";

    public static final String GET_ALL_INSTRUMENTS_WITH_INITIAL_PRICE = "SELECT i.id, i.name, i.ticker, i.fullname, i.market_name, t.name as type, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "left join instrument_prices i2p on i.ticker = i2p.ticker";
    public static final String GET_INSTRUMENT_WITH_INITIAL_PRICE = "SELECT i.id, i.name, i.ticker, i.fullname, i.market_name, t.name as type, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "left join instrument_prices i2p on i.ticker = i2p.ticker " +
            "where i.id = ?";
    public static final String GET_TOP_10_INSTRUMENTS = "SELECT i.id, i.name, i.ticker, i.fullname, t.name as type, i.market_name, i.min_quantity, i.leverage, i2p.buy, i2p.sell, i.created_at, i.updated_at " +
            "from instruments i " +
            "left join instrument_type t on i.type_id = t.id " +
            "left join instrument_prices i2p on i.ticker = i2p.ticker " +
            "limit 10";

    public static final String GET_TYPE_ID = "SELECT t.id " +
            "from instrument_type t " +
            "where t.name = ?";
}
