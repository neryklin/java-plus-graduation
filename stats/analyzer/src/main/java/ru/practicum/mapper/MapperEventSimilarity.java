package ru.practicum.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

@Component
public class MapperEventSimilarity {
    public EventSimilarity toEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .score(eventSimilarityAvro.getScore())
                .eventX(eventSimilarityAvro.getEventA())
                .eventY(eventSimilarityAvro.getEventB())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}