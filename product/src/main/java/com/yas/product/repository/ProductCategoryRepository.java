package com.yas.product.repository;

import com.yas.product.model.Category;
import com.yas.product.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
//    List<ProductCategory> findAllByCategory(Category category);
    @Query(value = "from ProductCategory pc where pc.product.name = :productName and pc.category =:category")
    Page<ProductCategory> getProductCategoryWithSearch(@Param("productName") String productName,
                                                       Pageable pageable , Category category);
    Page<ProductCategory> findAllByCategory(Pageable pageable,Category category);

}
