package practicum.event.event.model;

import lombok.Getter;
import lombok.Setter;
import practicum.interaction.enums.EventState;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AdminEventParams {
    private Set<Long> users;
    private Set<EventState> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

}
