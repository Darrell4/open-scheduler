package com.openscheduler.scheduler.user;

public record UserResponse(
        Long id,
        String email,
        String displayName,
        String timezone) {

    static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getTimezone());
    }
}
