package com.openscheduler.scheduler.slots;

import com.openscheduler.scheduler.availability.AvailabilityRule;
import com.openscheduler.scheduler.availability.AvailabilityRuleRepository;
import com.openscheduler.scheduler.booking.Booking;
import com.openscheduler.scheduler.booking.BookingRepository;
import com.openscheduler.scheduler.booking.BookingStatus;
import com.openscheduler.scheduler.eventtype.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes bookable slots: the owner's weekly availability expanded over a
 * date range, minus already-booked times, in the owner's timezone.
 */
@Service
@RequiredArgsConstructor
public class SlotService {

    /** Slots start on this grid within an availability window. */
    private static final Duration STEP = Duration.ofMinutes(30);

    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<Slot> availableSlots(EventType eventType, LocalDate from, LocalDate to) {
        ZoneId ownerZone = ZoneId.of(eventType.getUser().getTimezone());
        Duration duration = Duration.ofMinutes(eventType.getDurationMinutes());
        Instant now = clock.instant();

        List<AvailabilityRule> rules =
                availabilityRuleRepository.findAllByUserId(eventType.getUser().getId());

        Instant rangeStart = from.atStartOfDay(ownerZone).toInstant();
        Instant rangeEnd = to.plusDays(1).atStartOfDay(ownerZone).toInstant();
        List<Booking> bookings = bookingRepository
                .findAllByEventTypeUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        eventType.getUser().getId(), BookingStatus.CONFIRMED, rangeEnd, rangeStart);

        List<Slot> slots = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            for (AvailabilityRule rule : rules) {
                if (rule.getDayOfWeek() != date.getDayOfWeek()) {
                    continue;
                }
                ZonedDateTime windowStart = date.atTime(rule.getStartTime()).atZone(ownerZone);
                ZonedDateTime windowEnd = date.atTime(rule.getEndTime()).atZone(ownerZone);

                for (ZonedDateTime slotStart = windowStart;
                     !slotStart.plus(duration).isAfter(windowEnd);
                     slotStart = slotStart.plus(STEP)) {
                    Instant start = slotStart.toInstant();
                    Instant end = slotStart.plus(duration).toInstant();
                    if (start.isBefore(now)) {
                        continue;
                    }
                    if (!overlapsAny(bookings, start, end)) {
                        slots.add(new Slot(start, end));
                    }
                }
            }
        }
        slots.sort((a, b) -> a.startAt().compareTo(b.startAt()));
        return slots;
    }

    private boolean overlapsAny(List<Booking> bookings, Instant start, Instant end) {
        return bookings.stream().anyMatch(
                b -> b.getStartAt().isBefore(end) && start.isBefore(b.getEndAt()));
    }
}
