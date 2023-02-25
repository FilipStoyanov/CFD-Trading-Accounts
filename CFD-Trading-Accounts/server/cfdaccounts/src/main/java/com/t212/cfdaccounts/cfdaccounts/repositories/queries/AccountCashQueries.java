package com.t212.cfdaccounts.cfdaccounts.repositories.queries;

public final class AccountCashQueries {
    public static final String WITHDRAW_AMOUNT = "UPDATE account_cash\n " +
            "set balance = balance - ?, updated_at = current_timestamp " +
            "WHERE user_id = ?";

    public static final String DEPOSIT_AMOUNT = "UPDATE account_cash\n " +
            "set balance = balance + ?, updated_at = current_timestamp " +
            "WHERE user_id = ?";

    public static final String GET_ACCOUNT_CASH = "SELECT w.id, w.user_id, w.balance, w.created_at, w.updated_at \n " +
            "from account_cash w " +
            "WHERE w.user_id = ?";
}
