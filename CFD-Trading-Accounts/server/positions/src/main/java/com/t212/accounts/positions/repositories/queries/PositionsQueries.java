package com.t212.accounts.positions.repositories.queries;

public final class PositionsQueries {
    public static final String GET_ALL_POSITIONS = "SELECT a.user_id, i.id, i.name, i.ticker, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "right join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.is_closed = false";

    public static final String GET_CLOSE_POSITIONS = "SELECT a.user_id, i.name, i.ticker, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "right join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.is_closed = true";

    public static final String UPDATE_POSITION = "UPDATE account_positions " +
            "SET is_closed = true " +
            "where user_id = ? and instrument_id = ? and type = ? and is_closed = false";

    public static final String GET_BY_INSTRUMENT_ID = "SELECT a.user_id, i.ticker, a.instrument_id, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "left join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.instrument_id = ? and a.type = ? and is_closed = false";

    public static final String GET_UPDATED_POSITION_BY_ID = "SELECT a.user_id, i.name, i.ticker, a.instrument_id, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "left join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.instrument_id = ? and a.type = ? and a.is_closed = true ";

    public static final String INSERT_POSITION = "INSERT INTO\n" +
            " account_positions(user_id, instrument_id, quantity, type, buy_price, sell_price)\n" +
            " VALUES (?,?,?,?,?,?)";

    public static final String GET_ALL_OPEN_POSITIONS_WITH_CURRENT_PRICES = "SELECT a.user_id, i.name, i.ticker, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, i2p.buy as current_buy, i2p.sell as current_sell, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "right join instruments i on a.instrument_id = i.id " +
            "left join instrument_prices i2p on i.id = i2p.instrument_id " +
            "where a.user_id=? and a.is_closed = false";

    public static final String GET_ALL_TYPES_OF_POSITIONS = "SELECT i2t.name as type " +
            "from account_positions a " +
            "left join instruments i on a.instrument_id = i.id " +
            "left join instrument_type i2t on i.type_id = i2t.id " +
            "where a.user_id = ?";

}