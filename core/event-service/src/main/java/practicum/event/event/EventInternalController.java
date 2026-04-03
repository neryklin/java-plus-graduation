package practicum.event.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.event.event.dto.EventFullDto;
import practicum.event.event.service.EventService;

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


}
