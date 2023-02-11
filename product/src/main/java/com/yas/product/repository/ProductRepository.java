package com.yas.product.repository;

import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBrand(Brand brand);

    Optional<Product> findBySlug(String slug);

    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:productName% " +
            "AND (p.brand.name IN :brandName OR (:brandName is null OR :brandName = ''))")
    Page<Product> getProductsWithFilter(@Param("productName") String productName,
                                        @Param("brandName") String brandName,
                                        Pageable pageable);

    Page<Product> findByName(Pageable pageable, String productName);

    Page<Product> findByBrandName(Pageable pageable, String brandName);

    Page<Product> findByIsPublishedTrueAndIsVisibleIndividuallyTrue(Pageable pageable);

    @Query(value = "FROM Product p WHERE p.isFeatured = TRUE " +
            "AND p.isVisibleIndividually = TRUE " +
            "AND p.isPublished = TRUE ORDER BY p.lastModifiedOn DESC")
    Page<Product> getFeaturedProduct(Pageable pageable);
}
