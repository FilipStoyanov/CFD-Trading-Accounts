package com.t212.cfdaccounts.cfdaccounts.repositories.queries;

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

}