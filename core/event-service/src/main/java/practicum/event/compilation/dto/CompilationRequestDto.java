package practicum.event.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.event.event.model.Event;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationRequestDto {
    private Long id;

    private String title;

    private Boolean pinned;

    private List<Event> events;
}
