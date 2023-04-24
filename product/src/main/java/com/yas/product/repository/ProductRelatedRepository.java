package com.yas.product.repository;

import com.yas.product.model.Product;
import com.yas.product.model.ProductRelated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRelatedRepository extends JpaRepository<ProductRelated, Long> {
    Page<ProductRelated> findAllByProduct(Product product, Pageable pageable);
}
