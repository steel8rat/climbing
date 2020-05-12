package com.mokhov.climbing.services;

import com.mokhov.climbing.models.User;

import java.util.Optional;

public interface UserAuthenticationService {

    String createUuid(User user);

    Optional<User> findByToken(String token);

    /**
     * Logs out the given input {@code user}.
     *
     * @param user the user to logout
     */
    void logout(User user);
}

