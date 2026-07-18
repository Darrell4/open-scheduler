package com.openscheduler.scheduler.eventtype;

import com.openscheduler.scheduler.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class EventTypeServiceTest {

    private EventTypeRepository eventTypeRepository;
    private EventTypeService eventTypeService;
    private User owner;

    @BeforeEach
    void setUp() {
        eventTypeRepository = Mockito.mock(EventTypeRepository.class);
        when(eventTypeRepository.save(any(EventType.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventTypeRepository.findByUserIdAndSlug(any(), any())).thenReturn(Optional.empty());
        eventTypeService = new EventTypeService(eventTypeRepository);

        owner = new User();
        owner.setEmail("alice@example.com");
        owner.setDisplayName("Alice");
    }

    @ParameterizedTest
    @CsvSource({
            "'30-min Intro Call!', 30-min-intro-call",
            "'  Coffee   Chat  ', coffee-chat",
            "'Café & Croissants', cafe-croissants",
            "'!!!', event",
    })
    void slugifiesNames(String name, String expectedSlug) {
        assertThat(EventTypeService.slugify(name)).isEqualTo(expectedSlug);
    }

    @Test
    void createsEventTypeWithSlug() {
        EventType created = eventTypeService.create(owner,
                new CreateEventTypeRequest("Intro Call", "Short intro", 30));

        assertThat(created.getSlug()).isEqualTo("intro-call");
        assertThat(created.getName()).isEqualTo("Intro Call");
        assertThat(created.getDurationMinutes()).isEqualTo(30);
        assertThat(created.isActive()).isTrue();
        assertThat(created.getUser()).isSameAs(owner);
    }

    @Test
    void appendsSuffixWhenSlugTaken() {
        EventType existing = new EventType();
        when(eventTypeRepository.findByUserIdAndSlug(any(), eq("intro-call")))
                .thenReturn(Optional.of(existing));

        EventType created = eventTypeService.create(owner,
                new CreateEventTypeRequest("Intro Call", null, 30));

        assertThat(created.getSlug()).isEqualTo("intro-call-2");
    }
}
