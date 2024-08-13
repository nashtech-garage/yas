package com.yas.product.repository;

import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttributeValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    List<ProductAttributeValue> findAllByProduct(Product product);
}
