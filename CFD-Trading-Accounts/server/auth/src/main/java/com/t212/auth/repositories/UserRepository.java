package com.t212.auth.repositories;

import com.t212.auth.repositories.models.UserDAO;

public interface UserRepository {
    UserDAO getUserByUsernameAndPassword(String username, String password);
}
