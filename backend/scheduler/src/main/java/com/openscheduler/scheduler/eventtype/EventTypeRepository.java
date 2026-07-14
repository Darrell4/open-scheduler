package com.openscheduler.scheduler.eventtype;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    List<EventType> findAllByUserId(Long userId);

    Optional<EventType> findByUserIdAndSlug(Long userId, String slug);
}
