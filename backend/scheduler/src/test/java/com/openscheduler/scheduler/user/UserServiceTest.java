package com.openscheduler.scheduler.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenReturn("{hashed}");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registersUserWithNormalizedEmailAndHashedPassword() {
        User user = userService.register(
                new RegisterRequest("  Alice@Example.COM ", " Alice ", "secret-password", "Europe/Luxembourg"));

        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getDisplayName()).isEqualTo("Alice");
        assertThat(user.getPasswordHash()).isEqualTo("{hashed}");
        assertThat(user.getTimezone()).isEqualTo("Europe/Luxembourg");
    }

    @Test
    void defaultsTimezoneToUtcWhenMissing() {
        User user = userService.register(
                new RegisterRequest("bob@example.com", "Bob", "secret-password", null));

        assertThat(user.getTimezone()).isEqualTo("UTC");
    }

    @Test
    void rejectsInvalidTimezone() {
        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("bob@example.com", "Bob", "secret-password", "Not/AZone")))
                .isInstanceOf(InvalidTimezoneException.class);
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("Taken@example.com", "Taken", "secret-password", null)))
                .isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Test
    void duplicateCheckDoesNotSave() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("taken@example.com", "Taken", "secret-password", null)))
                .isInstanceOf(EmailAlreadyInUseException.class);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.never()).save(captor.capture());
    }
}
