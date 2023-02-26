package com.t212.auth.repositories.models;

public record UserDAO(long id, String username, String passwordHash, String nationalId, String email) {
}
