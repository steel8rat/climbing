package com.mokhov.climbing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "File extension not supported")
public class FileExtensionNotSupported extends RuntimeException {
    public FileExtensionNotSupported(String msg) {
        super(msg);
    }
}
