package com.t212.cfdaccounts.cfdaccounts.repositories.queries;

public final class UserQueries {
    public static final String INSERT_USER = "INSERT INTO users(username, password_hash, email, national_id) " +
            "VALUES (?,?,?,?)";
    public static final String GET_USER_BY_ID = "SELECT u.id, u.username, u.password_hash, u.email, u.national_id " +
            "from users u " +
            "where u.id = ?";
    public static final String LIST_USERS = "SELECT u.id, u.username, u.password_hash, u.email, u.national_id " +
            "from users u " +
            "limit ?,?";
    public static final String DELETE_USER = "delete from users where id=?";

    public static final String GET_USER_BY_USERNAME = "SELECT u.id, u.username, u.password_hash, u.email, u.national_id " +
            "from users u " +
            "where u.username = ?";
}