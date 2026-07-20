package com.openscheduler.scheduler.slots;

import com.openscheduler.scheduler.availability.AvailabilityRule;
import com.openscheduler.scheduler.availability.AvailabilityRuleRepository;
import com.openscheduler.scheduler.booking.Booking;
import com.openscheduler.scheduler.booking.BookingRepository;
import com.openscheduler.scheduler.eventtype.EventType;
import com.openscheduler.scheduler.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SlotServiceTest {

    // Monday 2026-07-20; "now" is far in the past relative to the test range
    // so no slots are filtered as "in the past" unless a test wants that.
    private static final LocalDate MONDAY = LocalDate.of(2026, 7, 27);
    private static final Instant NOW = Instant.parse("2026-07-20T00:00:00Z");

    private AvailabilityRuleRepository availabilityRuleRepository;
    private BookingRepository bookingRepository;
    private SlotService slotService;
    private User owner;
    private EventType eventType;

    @BeforeEach
    void setUp() {
        availabilityRuleRepository = Mockito.mock(AvailabilityRuleRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        slotService = new SlotService(availabilityRuleRepository, bookingRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        owner = new User();
        owner.setId(1L);
        owner.setTimezone("UTC");

        eventType = new EventType();
        eventType.setId(10L);
        eventType.setUser(owner);
        eventType.setDurationMinutes(60);

        when(bookingRepository
                .findAllByEventTypeUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        any(), any(), any(), any()))
                .thenReturn(List.of());
    }

    private void givenRules(AvailabilityRule... rules) {
        when(availabilityRuleRepository.findAllByUserId(eq(1L))).thenReturn(List.of(rules));
    }

    private AvailabilityRule rule(DayOfWeek day, String start, String end) {
        AvailabilityRule r = new AvailabilityRule();
        r.setUser(owner);
        r.setDayOfWeek(day);
        r.setStartTime(LocalTime.parse(start));
        r.setEndTime(LocalTime.parse(end));
        return r;
    }

    private Booking booking(String startIso, String endIso) {
        Booking b = new Booking();
        b.setStartAt(Instant.parse(startIso));
        b.setEndAt(Instant.parse(endIso));
        return b;
    }

    @Test
    void expandsAvailabilityIntoSlotsOnHalfHourGrid() {
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "11:00"));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        // 60-min slots on a 30-min grid within 09:00-11:00: 09:00, 09:30, 10:00
        assertThat(slots).containsExactly(
                new Slot(Instant.parse("2026-07-27T09:00:00Z"), Instant.parse("2026-07-27T10:00:00Z")),
                new Slot(Instant.parse("2026-07-27T09:30:00Z"), Instant.parse("2026-07-27T10:30:00Z")),
                new Slot(Instant.parse("2026-07-27T10:00:00Z"), Instant.parse("2026-07-27T11:00:00Z")));
    }

    @Test
    void skipsDaysWithoutRules() {
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "11:00"));

        // Tuesday has no rule
        List<Slot> slots = slotService.availableSlots(eventType, MONDAY.plusDays(1), MONDAY.plusDays(1));

        assertThat(slots).isEmpty();
    }

    @Test
    void excludesSlotsOverlappingBookings() {
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "11:00"));
        when(bookingRepository
                .findAllByEventTypeUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        any(), any(), any(), any()))
                .thenReturn(List.of(booking("2026-07-27T09:30:00Z", "2026-07-27T10:30:00Z")));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        // A 09:30-10:30 booking overlaps every 60-min candidate in 09:00-11:00.
        assertThat(slots).isEmpty();
    }

    @Test
    void keepsSlotsAdjacentToBookings() {
        eventType.setDurationMinutes(30);
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "10:30"));
        when(bookingRepository
                .findAllByEventTypeUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        any(), any(), any(), any()))
                .thenReturn(List.of(booking("2026-07-27T09:30:00Z", "2026-07-27T10:00:00Z")));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        assertThat(slots).containsExactly(
                new Slot(Instant.parse("2026-07-27T09:00:00Z"), Instant.parse("2026-07-27T09:30:00Z")),
                new Slot(Instant.parse("2026-07-27T10:00:00Z"), Instant.parse("2026-07-27T10:30:00Z")));
    }

    @Test
    void excludesSlotsInThePast() {
        // now = Monday 09:45 UTC on the queried day
        slotService = new SlotService(availabilityRuleRepository, bookingRepository,
                Clock.fixed(Instant.parse("2026-07-27T09:45:00Z"), ZoneOffset.UTC));
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "11:00"));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        assertThat(slots).containsExactly(
                new Slot(Instant.parse("2026-07-27T10:00:00Z"), Instant.parse("2026-07-27T11:00:00Z")));
    }

    @Test
    void respectsOwnerTimezone() {
        owner.setTimezone("Europe/Luxembourg"); // UTC+2 in July
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "10:00"));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        // 09:00 local = 07:00 UTC
        assertThat(slots).containsExactly(
                new Slot(Instant.parse("2026-07-27T07:00:00Z"), Instant.parse("2026-07-27T08:00:00Z")));
    }

    @Test
    void spansMultipleDays() {
        givenRules(
                rule(DayOfWeek.MONDAY, "09:00", "10:00"),
                rule(DayOfWeek.TUESDAY, "14:00", "15:00"));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY.plusDays(1));

        assertThat(slots).containsExactly(
                new Slot(Instant.parse("2026-07-27T09:00:00Z"), Instant.parse("2026-07-27T10:00:00Z")),
                new Slot(Instant.parse("2026-07-28T14:00:00Z"), Instant.parse("2026-07-28T15:00:00Z")));
    }

    @Test
    void slotTooLongForWindowYieldsNothing() {
        eventType.setDurationMinutes(120);
        givenRules(rule(DayOfWeek.MONDAY, "09:00", "10:00"));

        List<Slot> slots = slotService.availableSlots(eventType, MONDAY, MONDAY);

        assertThat(slots).isEmpty();
    }
}
