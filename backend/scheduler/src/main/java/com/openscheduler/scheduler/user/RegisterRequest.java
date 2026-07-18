package com.openscheduler.scheduler.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 255) String displayName,
        @NotBlank @Size(min = 8, max = 100) String password,
        @Size(max = 64) String timezone) {
}
