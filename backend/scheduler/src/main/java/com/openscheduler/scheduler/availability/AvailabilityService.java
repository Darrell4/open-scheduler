package com.openscheduler.scheduler.availability;

import com.openscheduler.scheduler.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRuleRepository availabilityRuleRepository;

    @Transactional
    public AvailabilityRule addRule(User owner, AvailabilityRuleRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new InvalidAvailabilityRuleException("startTime must be before endTime");
        }

        boolean overlaps = availabilityRuleRepository.findAllByUserId(owner.getId()).stream()
                .filter(rule -> rule.getDayOfWeek() == request.dayOfWeek())
                .anyMatch(rule -> rule.getStartTime().isBefore(request.endTime())
                        && request.startTime().isBefore(rule.getEndTime()));
        if (overlaps) {
            throw new InvalidAvailabilityRuleException(
                    "Rule overlaps an existing rule on " + request.dayOfWeek());
        }

        AvailabilityRule rule = new AvailabilityRule();
        rule.setUser(owner);
        rule.setDayOfWeek(request.dayOfWeek());
        rule.setStartTime(request.startTime());
        rule.setEndTime(request.endTime());
        return availabilityRuleRepository.save(rule);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityRule> listForUser(Long userId) {
        return availabilityRuleRepository.findAllByUserId(userId);
    }

    @Transactional
    public void deleteRule(User owner, Long ruleId) {
        AvailabilityRule rule = availabilityRuleRepository.findById(ruleId)
                .filter(r -> r.getUser().getId().equals(owner.getId()))
                .orElseThrow(() -> new AvailabilityRuleNotFoundException(ruleId));
        availabilityRuleRepository.delete(rule);
    }
}
