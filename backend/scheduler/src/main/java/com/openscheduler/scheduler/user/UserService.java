package com.openscheduler.scheduler.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setTimezone(validateTimezone(request.timezone()));
        return userRepository.save(user);
    }

    private String validateTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return "UTC";
        }
        try {
            return ZoneId.of(timezone.trim()).getId();
        } catch (Exception e) {
            throw new InvalidTimezoneException(timezone);
        }
    }
}
