package practicum.event.category;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practicum.event.category.dto.CategoryRequestDto;
import practicum.event.category.service.CategoryService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryRequestDto>> get(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CategoryRequestDto> categories = categoryService.getAll(from, size);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryRequestDto> getById(@PathVariable Long catId) {
        CategoryRequestDto categoryRequestDto = categoryService.getById(catId);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryRequestDto);
    }
}
