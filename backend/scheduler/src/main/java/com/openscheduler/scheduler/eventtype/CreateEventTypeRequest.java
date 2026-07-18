package com.openscheduler.scheduler.eventtype;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEventTypeRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        @NotNull @Min(5) @Max(480) Integer durationMinutes) {
}
