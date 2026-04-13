package ru.practicum.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;
import ru.practicum.model.ActionType;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.service.eventSimilarity.EventSimilarityServiceImpl;
import ru.practicum.service.userAction.UserActionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final EventSimilarityServiceImpl eventSimilarityService;
    private final UserActionService userActionService;
    private final UserActionRepository userActionRepository;

    private RecommendedEventProto newRecommendedEvent(Long eventId, Double scores) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(scores)
                .build();
    }

    private Double getActionWeight(ActionType actionType) {
        return switch (actionType) {
            case LIKE -> 1.0;
            case REGISTER -> 0.8;
            case VIEW -> 0.4;
        };
    }

    @Override
    public List<RecommendedEventProto> getRecommendations(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();
        PageRequest pageRequest = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<UserAction> userActionsList = userActionRepository.findAllByUserId(userId, pageRequest);
        if (userActionsList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> fullEvents = userActionsList.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        Set<Long> userEvents = userActionsList.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (userEvents.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> userEventIds = new HashSet<>(userActionService.findSortedEventIdsOfUser(request.getUserId(),
                maxResults));
        List<EventSimilarity> similarities = eventSimilarityService.findAllPairSimilarEvents(userEventIds, maxResults);
        return similarities.stream()
                .distinct()
                .filter(s -> (fullEvents.contains(s.getEventX()) ^ fullEvents.contains(s.getEventY())))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(maxResults)
                .map(s -> {
                    Long recommendedEventId = fullEvents.contains(s.getEventX()) ? s.getEventY() : s.getEventX();
                    return newRecommendedEvent(recommendedEventId, s.getScore());
                })
                .toList();
    }


    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Set<Long> userEventId = userActionRepository.findAllByUserId(request.getUserId()).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        return eventSimilarityRepository.findAllByEventXOrEventY(request.getEventId(), request.getEventId()).stream()
                .distinct()
                .filter(s -> !userEventId.contains(s.getEventX()) || !userEventId.contains(s.getEventY()))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(request.getMaxResults())
                .map(s -> {
                    Long recommendedEventId = s.getEventX().equals(request.getEventId()) ? s.getEventY() : s.getEventX();
                    return newRecommendedEvent(recommendedEventId, s.getScore());
                })
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());
        Map<Long, Double> eventScores = userActionRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(UserAction::getEventId,
                        Collectors.summingDouble(userAction -> getActionWeight(userAction.getActionType()))));
        return eventScores.entrySet().stream()
                .map(e -> newRecommendedEvent(e.getKey(), e.getValue()))
                .toList();
    }

}