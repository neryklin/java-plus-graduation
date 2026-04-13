package ru.practicum.service.eventSimilarity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.MapperEventSimilarity;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final MapperEventSimilarity mapperEventSimilarity;

    @Override
    public void saveSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        log.info(" start save sim event {}", eventSimilarityAvro);
        EventSimilarity event = mapperEventSimilarity.toEventSimilarity(eventSimilarityAvro);
        EventSimilarity savedEvent = eventSimilarityRepository.save(event);
        log.info("end save sim event {}", savedEvent);
    }
}