package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(String categoryId);
}