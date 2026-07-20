package com.openscheduler.scheduler.availability;

import com.openscheduler.scheduler.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AvailabilityServiceTest {

    private AvailabilityRuleRepository repository;
    private AvailabilityService service;
    private User owner;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(AvailabilityRuleRepository.class);
        when(repository.save(any(AvailabilityRule.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repository.findAllByUserId(any())).thenReturn(List.of());
        service = new AvailabilityService(repository);

        owner = new User();
        owner.setEmail("alice@example.com");
    }

    private AvailabilityRule rule(DayOfWeek day, String start, String end) {
        AvailabilityRule r = new AvailabilityRule();
        r.setUser(owner);
        r.setDayOfWeek(day);
        r.setStartTime(LocalTime.parse(start));
        r.setEndTime(LocalTime.parse(end));
        return r;
    }

    @Test
    void addsRule() {
        AvailabilityRule created = service.addRule(owner, new AvailabilityRuleRequest(
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));

        assertThat(created.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(created.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(created.getEndTime()).isEqualTo(LocalTime.of(17, 0));
    }

    @Test
    void rejectsStartAfterEnd() {
        assertThatThrownBy(() -> service.addRule(owner, new AvailabilityRuleRequest(
                DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(9, 0))))
                .isInstanceOf(InvalidAvailabilityRuleException.class);
    }

    @Test
    void rejectsOverlapOnSameDay() {
        when(repository.findAllByUserId(any()))
                .thenReturn(List.of(rule(DayOfWeek.MONDAY, "09:00", "12:00")));

        assertThatThrownBy(() -> service.addRule(owner, new AvailabilityRuleRequest(
                DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(14, 0))))
                .isInstanceOf(InvalidAvailabilityRuleException.class);
    }

    @Test
    void allowsSameTimesOnDifferentDay() {
        when(repository.findAllByUserId(any()))
                .thenReturn(List.of(rule(DayOfWeek.MONDAY, "09:00", "12:00")));

        AvailabilityRule created = service.addRule(owner, new AvailabilityRuleRequest(
                DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));

        assertThat(created.getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }

    @Test
    void allowsAdjacentRules() {
        when(repository.findAllByUserId(any()))
                .thenReturn(List.of(rule(DayOfWeek.MONDAY, "09:00", "12:00")));

        AvailabilityRule created = service.addRule(owner, new AvailabilityRuleRequest(
                DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(17, 0)));

        assertThat(created.getStartTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void deleteRejectsForeignRule() {
        owner.setId(1L);
        User other = new User();
        other.setId(2L);
        AvailabilityRule foreign = new AvailabilityRule();
        foreign.setUser(other);

        when(repository.findById(42L)).thenReturn(Optional.of(foreign));

        assertThatThrownBy(() -> service.deleteRule(owner, 42L))
                .isInstanceOf(AvailabilityRuleNotFoundException.class);
    }
}
