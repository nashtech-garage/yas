package com.yas.product.repository;


import com.yas.product.model.Product;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.viewmodel.productoption.ProductOptionValueProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {
    List<ProductOptionValue> findAllByProduct(Product product);
    
    List<ProductOptionValueProjection> findAllProjectedByProduct(Product product);
    
}
