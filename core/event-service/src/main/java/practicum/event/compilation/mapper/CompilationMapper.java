package practicum.event.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.event.compilation.dto.CompilationCreateDto;
import practicum.event.compilation.dto.CompilationRequestDto;
import practicum.event.compilation.model.Compilation;
import practicum.event.event.model.Event;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static Compilation toEntity(CompilationCreateDto compilationCreateDto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationCreateDto.getTitle());
        compilation.setPinned(compilationCreateDto.getPinned());
        compilation.setEvents(events);

        return compilation;
    }

    public static CompilationRequestDto toRequestDto(Compilation compilation) {
        return new CompilationRequestDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilation.getEvents()
        );
    }
}
