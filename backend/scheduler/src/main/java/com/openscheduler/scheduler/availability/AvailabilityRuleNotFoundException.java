package com.openscheduler.scheduler.availability;

public class AvailabilityRuleNotFoundException extends RuntimeException {

    public AvailabilityRuleNotFoundException(Long id) {
        super("Availability rule not found: " + id);
    }
}
