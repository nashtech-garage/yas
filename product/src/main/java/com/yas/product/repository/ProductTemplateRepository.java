package com.yas.product.repository;

import com.yas.product.model.attribute.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate,Long> {

    @Query("select e from ProductTemplate e where e.name = ?1 and (?2 is null or e.id <> ?2)")
    ProductTemplate findExistedName(String name, Long id);
}
