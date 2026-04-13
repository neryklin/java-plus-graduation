package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    List<EventSimilarity> findAllByEventXOrEventY(Long eventA, Long eventB);

    @Query("""
            select s
            from EventSimilarity s
            where s.eventX in :eventIds or s.eventY in :eventIds
            order by s.score desc
            """)
    List<EventSimilarity> findAllPairSimilarEvents(Set<Long> eventIds, Pageable pageable);
}