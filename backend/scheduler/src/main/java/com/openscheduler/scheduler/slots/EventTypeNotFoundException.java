package com.openscheduler.scheduler.slots;

public class EventTypeNotFoundException extends RuntimeException {

    public EventTypeNotFoundException(String detail) {
        super("Event type not found: " + detail);
    }
}
