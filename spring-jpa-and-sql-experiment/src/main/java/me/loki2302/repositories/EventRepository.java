package me.loki2302.repositories;

import me.loki2302.entities.Event;
import me.loki2302.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);
}
