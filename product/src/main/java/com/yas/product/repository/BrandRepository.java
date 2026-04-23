package com.yas.product.repository;

import com.yas.product.model.Brand;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findBySlug(String slug);

    @Query("select e from Brand e where e.name = ?1 and (?2 is null or e.id != ?2)")
    Brand findExistedName(String name, Long id);

    List<Brand> findByNameContainingIgnoreCase(String name);
}
