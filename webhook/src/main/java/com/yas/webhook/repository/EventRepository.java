package com.yas.webhook.repository;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.enums.EventName;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = {"webhookEvents.webhook"})
    Optional<Event> findByName(EventName name);

}
