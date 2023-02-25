package com.t212.cfdaccounts.cfdaccounts.core.models;

public class User {
    public final long id;
    public final String username;
    public final String passwordHash;
    public final String nationalId;
    public final String email;

    public User(long id, String username, String passwordHash, String nationalId, String email) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.nationalId = nationalId;
    }
}
