package com.openscheduler.scheduler.eventtype;

import com.openscheduler.scheduler.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    @Transactional
    public EventType create(User owner, CreateEventTypeRequest request) {
        String baseSlug = slugify(request.name());
        String slug = uniqueSlug(owner.getId(), baseSlug);

        EventType eventType = new EventType();
        eventType.setUser(owner);
        eventType.setName(request.name().trim());
        eventType.setDescription(request.description());
        eventType.setDurationMinutes(request.durationMinutes());
        eventType.setSlug(slug);
        return eventTypeRepository.save(eventType);
    }

    @Transactional(readOnly = true)
    public List<EventType> listForUser(Long userId) {
        return eventTypeRepository.findAllByUserId(userId);
    }

    /**
     * Turns a display name into a URL-friendly slug:
     * "30-min Intro Call!" -> "30-min-intro-call".
     */
    static String slugify(String name) {
        String normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return slug.isEmpty() ? "event" : slug;
    }

    private String uniqueSlug(Long userId, String baseSlug) {
        String candidate = baseSlug;
        int suffix = 2;
        while (eventTypeRepository.findByUserIdAndSlug(userId, candidate).isPresent()) {
            candidate = baseSlug + "-" + suffix++;
        }
        return candidate;
    }
}
