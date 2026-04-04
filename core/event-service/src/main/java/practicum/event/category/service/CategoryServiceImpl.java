package practicum.event.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.event.category.dto.CategoryCreateDto;
import practicum.event.category.dto.CategoryRequestDto;
import practicum.event.category.mapper.CategoryMapper;
import practicum.event.category.model.Category;
import practicum.event.category.storage.CategoryRepository;
import practicum.event.event.storage.EventRepository;
import practicum.interaction.exception.CategoryNotFoundException;
import practicum.interaction.exception.ConflictException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryRequestDto getById(Long catId) {
        Category category = getCategoryOrThrow(catId);

        return CategoryMapper.toCategoryRequestDto(
                categoryRepository.save(category)
        );
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
    }

    @Override
    @Transactional
    public CategoryRequestDto create(CategoryCreateDto categoryCreateDto) {
        return CategoryMapper.toCategoryRequestDto(
                categoryRepository.save(CategoryMapper.toCategory(categoryCreateDto)));
    }

    @Override
    @Transactional
    public CategoryRequestDto update(CategoryCreateDto categoryCreateDto, Long catId) {
        Category category = getCategoryOrThrow(catId);
        category.setName(categoryCreateDto.getName());
        return CategoryMapper.toCategoryRequestDto(
                categoryRepository.save(category)
        );
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        Category category = getCategoryOrThrow(catId);
        if (eventRepository.findFirstByCategoryId(catId).isPresent()) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryRequestDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> page = categoryRepository.findAll(pageable);
        return page.stream()
                .map(CategoryMapper::toCategoryRequestDto)
                .collect(Collectors.toList());
    }

}
