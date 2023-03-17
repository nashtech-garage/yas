package com.yas.product.repository;

import com.yas.product.model.attribute.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    @Query("select e from ProductAttribute e where e.name = ?1 and (?2 is null or e.id != ?2)")
    ProductAttribute findByNameAndId(String name, Long id);
}
