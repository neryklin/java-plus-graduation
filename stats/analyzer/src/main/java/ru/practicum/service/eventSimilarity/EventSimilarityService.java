package ru.practicum.service.eventSimilarity;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityService {
    void saveSimilarity(EventSimilarityAvro eventSimilarityAvro);


    public List<EventSimilarity> findAllPairSimilarEvents(Set<Long> eventIds, int maxResults);


}
