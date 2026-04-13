package practicum.event.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import practicum.event.event.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByIdIn(Set<Long> eventIds);

    Optional<Event> findFirstByCategoryId(Long categoryId);
}
