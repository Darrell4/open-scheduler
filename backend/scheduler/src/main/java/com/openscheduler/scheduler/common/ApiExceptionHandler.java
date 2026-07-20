package com.openscheduler.scheduler.common;

import com.openscheduler.scheduler.availability.AvailabilityRuleNotFoundException;
import com.openscheduler.scheduler.availability.InvalidAvailabilityRuleException;
import com.openscheduler.scheduler.slots.EventTypeNotFoundException;
import com.openscheduler.scheduler.slots.InvalidSlotRangeException;
import com.openscheduler.scheduler.user.EmailAlreadyInUseException;
import com.openscheduler.scheduler.user.InvalidTimezoneException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain exceptions to RFC 7807 problem-detail responses.
 * Bean validation errors are handled by Spring's default handler (400).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    ProblemDetail handleEmailInUse(EmailAlreadyInUseException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidTimezoneException.class)
    ProblemDetail handleInvalidTimezone(InvalidTimezoneException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidAvailabilityRuleException.class)
    ProblemDetail handleInvalidAvailabilityRule(InvalidAvailabilityRuleException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AvailabilityRuleNotFoundException.class)
    ProblemDetail handleAvailabilityRuleNotFound(AvailabilityRuleNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(EventTypeNotFoundException.class)
    ProblemDetail handleEventTypeNotFound(EventTypeNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidSlotRangeException.class)
    ProblemDetail handleInvalidSlotRange(InvalidSlotRangeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
