package practicum.event.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import practicum.event.category.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
}
