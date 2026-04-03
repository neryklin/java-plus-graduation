package practicum.event.category.service;

import practicum.event.category.dto.CategoryCreateDto;
import practicum.event.category.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryRequestDto create(CategoryCreateDto categoryCreateDto);

    CategoryRequestDto update(CategoryCreateDto categoryCreateDto, Long catId);

    void delete(Long catId);

    List<CategoryRequestDto> getAll(int from, int size);

    CategoryRequestDto getById(Long catId);

}
