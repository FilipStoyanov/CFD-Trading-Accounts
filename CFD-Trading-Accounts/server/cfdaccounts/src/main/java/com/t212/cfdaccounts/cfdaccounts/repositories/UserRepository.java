package com.t212.cfdaccounts.cfdaccounts.repositories;

import com.t212.cfdaccounts.cfdaccounts.repositories.models.UserDAO;
import java.util.List;

public interface UserRepository {
    UserDAO createUser(String username, String email, String nationalID, String password);

    UserDAO getUser(long id);

    List<UserDAO> listUsers(Integer page, Integer pageSize);

    boolean deleteUser(long id);

    UserDAO getUserByUsername(String username);
}
