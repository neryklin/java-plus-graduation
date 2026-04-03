package practicum.event.event.model;

import lombok.Getter;
import lombok.Setter;
import practicum.interaction.enums.EventPublicSort;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class PublicEventParams {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventPublicSort sort;
    private Integer from;
    private Integer size;
    private String ipAdr;
}
