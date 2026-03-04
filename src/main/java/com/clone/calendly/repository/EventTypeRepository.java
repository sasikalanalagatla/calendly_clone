package com.clone.calendly.repository;

import com.clone.calendly.model.EventType;
import com.clone.calendly.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    List<EventType> findByUser(User user);
}
