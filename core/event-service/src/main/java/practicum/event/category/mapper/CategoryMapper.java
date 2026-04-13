package practicum.event.category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.event.category.dto.CategoryCreateDto;
import practicum.event.category.dto.CategoryRequestDto;
import practicum.event.category.model.Category;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {
    public static Category toCategory(CategoryCreateDto categoryCreateDto) {
        Category category = new Category();
        category.setName(categoryCreateDto.getName());

        return category;
    }

    public static CategoryRequestDto toCategoryRequestDto(Category category) {
        return new CategoryRequestDto(
                category.getId(),
                category.getName()
        );
    }
}
