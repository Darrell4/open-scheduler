package com.openscheduler.scheduler.eventtype;

public record EventTypeResponse(
        Long id,
        String name,
        String slug,
        String description,
        Integer durationMinutes,
        boolean active) {

    static EventTypeResponse from(EventType eventType) {
        return new EventTypeResponse(
                eventType.getId(),
                eventType.getName(),
                eventType.getSlug(),
                eventType.getDescription(),
                eventType.getDurationMinutes(),
                eventType.isActive());
    }
}
