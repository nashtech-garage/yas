package com.yas.product.repository;

import com.yas.product.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    @Query("select e from Category e where e.name = ?1 and (?2 is null or e.id != ?2)")
    Category findExistedName(String name, Long id);

    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Retrieves a list of category names ordered by the number of associated products in descending order.
     * Limits the results based on the given {@link Pageable} parameter.
     *
     * @param pageable specifies the number of results and pagination details.
     * @return a list of category names sorted by product count in descending order.
     */
    @Query("SELECT c.name FROM Category c "
        + "JOIN c.productCategories pc "
        + "GROUP BY c.name "
        + "ORDER BY COUNT(pc.product.id) DESC")
    List<String> findCategoriesOrderedByProductCount(Pageable pageable);

}
