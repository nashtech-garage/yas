package com.yas.product.repository;

import com.yas.product.model.Product;
import com.yas.product.model.ProductOptionCombination;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionCombinationRepository extends JpaRepository<ProductOptionCombination, Long> {
    List<ProductOptionCombination> findAllByProduct(Product product);

    Optional<ProductOptionCombination> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}
