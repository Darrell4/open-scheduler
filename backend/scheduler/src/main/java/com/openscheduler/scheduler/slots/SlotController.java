package com.openscheduler.scheduler.slots;

import com.openscheduler.scheduler.eventtype.EventType;
import com.openscheduler.scheduler.eventtype.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Public (unauthenticated) endpoint used by booking pages to show free slots.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class SlotController {

    /** Guard: a booker may look at most this many days ahead per request. */
    private static final int MAX_RANGE_DAYS = 60;

    private final EventTypeRepository eventTypeRepository;
    private final SlotService slotService;

    @GetMapping("/users/{userId}/event-types/{slug}/slots")
    public List<Slot> slots(@PathVariable Long userId,
                            @PathVariable String slug,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (to.isBefore(from) || from.plusDays(MAX_RANGE_DAYS).isBefore(to)) {
            throw new InvalidSlotRangeException(
                    "Range must be ascending and at most " + MAX_RANGE_DAYS + " days");
        }

        EventType eventType = eventTypeRepository.findByUserIdAndSlug(userId, slug)
                .filter(EventType::isActive)
                .orElseThrow(() -> new EventTypeNotFoundException(userId + "/" + slug));

        return slotService.availableSlots(eventType, from, to);
    }
}
