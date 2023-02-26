package com.t212.auth.core;

import com.t212.auth.api.rest.models.UserOutput;
import com.t212.auth.repositories.models.UserDAO;

public class Mappers {
    public static UserOutput fromResultSetToUserOutput(UserDAO user) {
        return new UserOutput(user.id(), user.username(), user.nationalId(), user.email());
    }
}
