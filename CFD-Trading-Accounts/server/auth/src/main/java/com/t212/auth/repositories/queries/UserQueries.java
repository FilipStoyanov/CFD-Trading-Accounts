package com.t212.auth.repositories.queries;

public final class UserQueries {
    public static final String GET_USER_BY_USERNAME = "SELECT u.id, u.username, u.password_hash, u.email, u.national_id " +
            "from users u " +
            "where u.username = ?";
}
