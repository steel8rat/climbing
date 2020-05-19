package com.mokhov.climbing.exceptions;

public class S3ObjectNotFound extends Exception {
    public S3ObjectNotFound(String msg) {
        super(msg);
    }
}
