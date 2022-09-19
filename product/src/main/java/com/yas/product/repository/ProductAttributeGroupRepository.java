package com.yas.product.repository;

import com.yas.product.model.Brand;
import com.yas.product.model.attribute.ProductAttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductAttributeGroupRepository extends JpaRepository<ProductAttributeGroup, Long> {
}
