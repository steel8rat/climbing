package com.mokhov.climbing.services;

import com.mokhov.climbing.config.UserConfig;
import com.mokhov.climbing.exceptions.UserNotFoundException;
import com.mokhov.climbing.models.User;
import com.mokhov.climbing.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConfig userConfig;

    public User getMongoUser(@NonNull String id) throws UserNotFoundException {
        Optional<User> optionalOfUser = userRepository.findById(id);
        if (!optionalOfUser.isPresent()) throw new UserNotFoundException(String.format("jwtUser with id [%s] isn't found", id));
        return optionalOfUser.get();
    }

    public String checkFullname(@NonNull String fullName) {
        if (fullName.length() > userConfig.getFullNameMaxChar())
            return String.format("Must be less than %s characters", userConfig.getFullNameMaxChar());
        // No issues found
        return "";
    }
}
