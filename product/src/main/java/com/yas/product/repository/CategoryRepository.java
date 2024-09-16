package com.yas.product.repository;

import com.yas.product.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    @Query("select e from Category e where e.name = ?1 and (?2 is null or e.id != ?2)")
    Category findExistedName(String name, Long id);

    List<Category> findByNameContainingIgnoreCase(String name);
}
