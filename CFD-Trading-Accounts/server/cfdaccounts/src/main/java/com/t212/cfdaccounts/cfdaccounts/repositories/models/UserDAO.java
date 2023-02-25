package com.t212.cfdaccounts.cfdaccounts.repositories.models;

public class UserDAO {

    public final long id;
    public final String username;
    public final String passwordHash;
    public final String nationalId;
    public final String email;

    public UserDAO(long id, String username, String passwordHash, String nationalId, String email) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.nationalId = nationalId;
    }
}
