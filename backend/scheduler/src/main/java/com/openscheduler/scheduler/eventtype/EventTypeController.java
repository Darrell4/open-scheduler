package com.openscheduler.scheduler.eventtype;

import com.openscheduler.scheduler.user.User;
import com.openscheduler.scheduler.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventTypeResponse create(Authentication authentication,
                                    @Valid @RequestBody CreateEventTypeRequest request) {
        return EventTypeResponse.from(eventTypeService.create(currentUser(authentication), request));
    }

    @GetMapping
    public List<EventTypeResponse> list(Authentication authentication) {
        return eventTypeService.listForUser(currentUser(authentication).getId()).stream()
                .map(EventTypeResponse::from)
                .toList();
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + authentication.getName()));
    }
}
