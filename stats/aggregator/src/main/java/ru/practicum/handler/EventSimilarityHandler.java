package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.AggregationService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityHandler {
    private final Map<Long, Map<Long, Double>> userActionWeights = new HashMap<>();
    private final Map<Long, Double> sumEventWeight = new HashMap<>();
    private final Map<Long, Map<Long, Double>> sumMinWeights = new HashMap<>();

    private final AggregationService aggregationService;

    public void handle(UserActionAvro userAction) {
        log.info("get userAction from agregate {}", userAction);
        Long userId = userAction.getUserId();
        Long eventId = userAction.getEventId();
        String userActionType = userAction.getActionType().toString();
        Double newWeight = userActionType.equals("LIKE") ? 1.0 : (
                userActionType.equals("REGISTER") ? 0.8 : (
                        userActionType.equals("VIEW") ? 0.4 : 0.0
                )
        );
        Instant timestamp = userAction.getTimestamp();

        Map<Long, Double> userWeight = userActionWeights.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userWeight.get(userId);

        if (oldWeight != null && oldWeight >= newWeight) {
            log.debug("weight not changeed: {},  {}", oldWeight, newWeight);
            return;

        }
        userWeight.put(userId, newWeight);
        Double sumWeight = userActionWeights.get(eventId).values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        sumEventWeight.put(eventId, sumWeight);
        List<EventSimilarityAvro> similarities = updateCheckSimilarities(eventId, userId, newWeight, oldWeight, timestamp);
        if (!similarities.isEmpty()) {
            log.info("send similar to kafka: {}", similarities);
            aggregationService.sendToKafkaEventSimilarities(similarities);
        }
    }

    private Double calculate(Long minEventId, Long maxEventId) {
        Double sumMin = sumMinWeights.get(minEventId).get(maxEventId);
        Double sumX = sumEventWeight.get(minEventId);
        Double sumY = sumEventWeight.get(maxEventId);
        if (sumMin == null || sumX == null || sumY == null) {
            log.debug("not calcilate error date ({}, {})", minEventId, maxEventId);
            return null;
        }
        if (sumX == 0 || sumY == 0 || sumMin == 0) {
            return 0.0;
        }
        return sumMin / Math.sqrt(sumX * sumY);
    }

    private List<EventSimilarityAvro> updateCheckSimilarities(Long eventId, Long userId, Double newWeight,
                                                              Double oldWeight, Instant timestamp) {
        List<EventSimilarityAvro> eventSimilarities = new ArrayList<>();
        for (Long userEventId : userActionWeights.keySet()) {
            if (userEventId.equals(eventId)) {
                log.debug("not update {}", eventId);
                continue;
            }
            Map<Long, Double> usersWeight = userActionWeights.get(userEventId);
            if (!usersWeight.containsKey(userId)) {
                log.debug("not update {}", eventId);
                continue;
            }
            Double deltaFromMinWeight = 0.0;
            double oldW = (oldWeight != null) ? oldWeight : 0.0;
            Double oldMin = Math.min(oldW, usersWeight.get(userId));
            Double newMin = Math.min(newWeight, usersWeight.get(userId));
            deltaFromMinWeight = newMin - oldMin;
            Long minEventId = Math.min(userEventId, eventId);
            Long maxEventId = Math.max(userEventId, eventId);
            if (deltaFromMinWeight != 0) {
                sumMinWeights.computeIfAbsent(minEventId, k -> new HashMap<>()).merge(maxEventId, deltaFromMinWeight, Double::sum);
            }
            Double score = calculate(minEventId, maxEventId);
            EventSimilarityAvro eventsSimilarityAvro = createSimilarityEvent(minEventId, maxEventId, score, timestamp);
            eventSimilarities.add(eventsSimilarityAvro);
            log.debug("update OK: {}", eventsSimilarityAvro);
        }

        return eventSimilarities;
    }

    private EventSimilarityAvro createSimilarityEvent(Long minId, Long maxId, Double score, Instant timestamp) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(minId)
                .setEventB(maxId)
                .setScore(score)
                .setTimestamp(timestamp)
                .build();
    }


}