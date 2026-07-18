package com.openscheduler.scheduler.user;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("Email already in use: " + email);
    }
}
