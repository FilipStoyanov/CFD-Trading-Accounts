package com.t212.auth.core;

import com.t212.auth.api.rest.models.LoginInput;
import com.t212.auth.api.rest.models.UserOutput;
import com.t212.auth.repositories.UserRepository;
import com.t212.auth.repositories.models.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserOutput findUserByUsernameAndPassword(LoginInput loginCredentials) {

        final UserDAO user = userRepository.getUserByUsernameAndPassword(loginCredentials.username, loginCredentials.password);
        boolean isValidPassword = BCrypt.checkpw(loginCredentials.password, user.passwordHash);
        if (isValidPassword) {
            return Mappers.fromResultSetToUserOutput(user);
        }
        return null;
    }
}