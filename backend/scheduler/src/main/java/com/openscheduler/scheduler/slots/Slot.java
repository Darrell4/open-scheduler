package com.openscheduler.scheduler.slots;

import java.time.Instant;

/**
 * A bookable time window, in UTC instants.
 */
public record Slot(Instant startAt, Instant endAt) {
}
