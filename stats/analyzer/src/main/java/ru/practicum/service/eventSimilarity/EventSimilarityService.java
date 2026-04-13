package ru.practicum.service.eventSimilarity;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {
    void saveSimilarity(EventSimilarityAvro eventSimilarityAvro);
}
