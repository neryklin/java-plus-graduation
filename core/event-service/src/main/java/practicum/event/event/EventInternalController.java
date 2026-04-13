package practicum.event.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.event.event.dto.EventFullDto;
import practicum.event.event.service.EventService;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventInternalController {
    private final EventService eventService;

    @GetMapping("/{id}/internal")
    public ResponseEntity<EventFullDto> getByIdInternal(@PathVariable("id") Long eventId) {
        EventFullDto event = eventService.getByIdInt(eventId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventFullDto> updateInternal(@RequestBody @Valid EventFullDto eventUpdateDto,
                                                       @PathVariable("id") Long eventId) {
        EventFullDto event = eventService.updateInt(eventId, eventUpdateDto);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<EventFullDto> setLike(@RequestHeader("X-EWM-USER-ID") long userId,
                                                @PathVariable("id") Long eventId) {
        log.info("--> PUT запрос /events/{}/like ", userId);
        EventFullDto event = eventService.setLike(eventId, userId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<EventFullDto>> getRecommendations(@RequestHeader("X-EWM-USER-ID") long userId,
                                                                 @RequestParam(defaultValue = "10") int maxResults) {
        log.info("--> GET запрос /events/recommendations {}", userId);
        List<EventFullDto> events = eventService.getRecommendations(userId, maxResults);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);

    }

    @GetMapping("/interactions")
    public ResponseEntity<List<Double>> getInteractions(@RequestParam(required = false) Set<Long> eventsIds) {
        log.info("--> GET запрос /events/interactions?eventsIds={}", eventsIds);
        List<RecommendedEventProto> results = eventService.getInteractions(eventsIds);
        List<Double> scores = results.stream()
                .map(RecommendedEventProto::getScore)
                .toList();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scores);

    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<EventFullDto>> getSimilarEvents(@RequestHeader("X-EWM-USER-ID") long userId,
                                                               @PathVariable("id") Long eventId,
                                                               @RequestParam(defaultValue = "10") int maxResults) {
        log.info("--> GET запрос /events/{}/similar", userId);
        List<EventFullDto> events = eventService.getSimilarEvents(eventId, userId, maxResults);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(events);
    }
}
