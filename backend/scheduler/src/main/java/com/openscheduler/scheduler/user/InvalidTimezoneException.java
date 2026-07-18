package com.openscheduler.scheduler.user;

public class InvalidTimezoneException extends RuntimeException {

    public InvalidTimezoneException(String timezone) {
        super("Invalid timezone: " + timezone);
    }
}
