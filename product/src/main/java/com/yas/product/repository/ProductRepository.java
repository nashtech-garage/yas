package com.yas.product.repository;

import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBrand(Brand brand);
}
