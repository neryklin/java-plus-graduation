package practicum.event.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practicum.event.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
