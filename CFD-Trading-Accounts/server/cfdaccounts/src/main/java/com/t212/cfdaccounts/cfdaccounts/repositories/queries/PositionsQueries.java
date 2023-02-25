package com.t212.cfdaccounts.cfdaccounts.repositories.queries;

public final class PositionsQueries {
    public static final String GET_ALL_POSITIONS = "SELECT a.user_id, i.name, i.ticker, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "right join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.is_closed = false";

    public static final String UPDATE_POSITION = "UPDATE account_positions " +
            "SET is_closed = true, deleted_at=current_timestamp" +
            "where user_id = ? and instrument_id = ? and is_closed = false";

    public static final String GET_BY_INSTRUMENT_ID = "SELECT a.user_id, a.instrument_id, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "where a.user_id=? and a.instrument_id = ? and is_closed = false";

    public static final String GET_UPDATED_POSITION_BY_ID = "SELECT a.user_id, i.name, i.ticker, a.instrument_id, a.quantity, a.type, a.buy_price, " +
            "a.sell_price, a.is_closed, a.created_at, a.updated_at, a.deleted_at " +
            "from account_positions a " +
            "left join instruments i on a.instrument_id = i.id " +
            "where a.user_id=? and a.instrument_id = ? and a.is_closed = true " +
            "order by a.id desc " +
            "limit 1";

    public static final String INSERT_POSITION = "INSERT INTO\n" +
            " account_positions(user_id, instrument_id, quantity, type, buy_price, sell_price)\n" +
            " VALUES (?,?,?,?,?,?)";
}
