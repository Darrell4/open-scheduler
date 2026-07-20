package com.openscheduler.scheduler.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByEventTypeIdAndStatusAndStartAtBetween(
            Long eventTypeId, BookingStatus status, Instant from, Instant to);

    /**
     * Overlap check: an existing booking conflicts if it starts before the
     * candidate slot ends and ends after the candidate slot starts.
     */
    boolean existsByEventTypeIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Long eventTypeId, BookingStatus status, Instant end, Instant start);

    /**
     * All bookings of an owner (across all event types) overlapping a range.
     * A booking on any event type makes the owner busy for that time.
     */
    List<Booking> findAllByEventTypeUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Long userId, BookingStatus status, Instant end, Instant start);
}
