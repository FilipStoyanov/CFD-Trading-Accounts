package com.t212.auth.repositories.mariadb;

import com.t212.auth.repositories.UserRepository;
import com.t212.auth.repositories.models.UserDAO;
import com.t212.auth.repositories.queries.UserQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MariaDBUserRepository implements UserRepository {
    private final TransactionTemplate txTemplate;
    private final JdbcTemplate jdbc;

    public MariaDBUserRepository(TransactionTemplate txTemplate, JdbcTemplate jdbc) {
        this.txTemplate = txTemplate;
        this.jdbc = jdbc;
    }

    @Override
    public UserDAO getUserByUsernameAndPassword(String username, String password) throws EmptyResultDataAccessException {
        return jdbc.queryForObject(UserQueries.GET_USER_BY_USERNAME, (rs, rowNum) -> fromResultSetToUser(rs), username);
    }

    private static UserDAO fromResultSetToUser(ResultSet rs) throws SQLException {
        return new UserDAO(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("email"),
                rs.getString("national_id")
        );
    }

}
