package practicum.event.compilation.service;

import practicum.event.compilation.dto.CompilationCreateDto;
import practicum.event.compilation.dto.CompilationRequestDto;
import practicum.event.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationRequestDto create(CompilationCreateDto compilationCreateDto);

    CompilationRequestDto update(CompilationUpdateDto compilationUpdateDto, Long compId);

    void delete(Long compId);

    List<CompilationRequestDto> get(Boolean pinned, int from, int size);

    CompilationRequestDto getById(Long compId);
}
