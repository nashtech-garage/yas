package com.yas.product.repository;

import com.yas.product.model.attribute.ProductAttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeGroupRepository extends JpaRepository<ProductAttributeGroup, Long> {
    @Query("select e from ProductAttributeGroup e where e.name = ?1 and (?2 is null or e.id != ?2)")
    ProductAttributeGroup findByNameAndId(String name, Long id);
}
