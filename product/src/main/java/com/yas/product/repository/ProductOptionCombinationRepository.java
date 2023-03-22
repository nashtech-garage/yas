package com.yas.product.repository;

import com.yas.product.model.Product;
import com.yas.product.model.ProductOptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionCombinationRepository extends JpaRepository<ProductOptionCombination, Long> {
    List<ProductOptionCombination> findAllByProduct(Product product);
}
