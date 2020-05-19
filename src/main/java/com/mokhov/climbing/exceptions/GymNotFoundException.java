package com.mokhov.climbing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Gym not found")
public class GymNotFoundException extends Exception {
    public GymNotFoundException(String msg) {
        super(msg);
    }
}
