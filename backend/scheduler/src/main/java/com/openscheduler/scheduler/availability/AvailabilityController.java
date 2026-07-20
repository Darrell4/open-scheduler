package com.openscheduler.scheduler.availability;

import com.openscheduler.scheduler.user.User;
import com.openscheduler.scheduler.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityRuleResponse add(Authentication authentication,
                                        @Valid @RequestBody AvailabilityRuleRequest request) {
        return AvailabilityRuleResponse.from(
                availabilityService.addRule(currentUser(authentication), request));
    }

    @GetMapping
    public List<AvailabilityRuleResponse> list(Authentication authentication) {
        return availabilityService.listForUser(currentUser(authentication).getId()).stream()
                .map(AvailabilityRuleResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long id) {
        availabilityService.deleteRule(currentUser(authentication), id);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + authentication.getName()));
    }
}
