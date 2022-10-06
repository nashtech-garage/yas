package com.yas.product.repository;


import com.yas.product.model.Product;
import com.yas.product.model.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
    List<ProductOptionValue> findAllByProduct(Product product);
}
