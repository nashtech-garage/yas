package com.yas.product.repository;

import com.yas.product.model.attribute.ProductAttributeTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeTemplateRepository extends JpaRepository<ProductAttributeTemplate, Long> {
    List<ProductAttributeTemplate> findAllByProductTemplateId(Long productTemplateId);
}
