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
    List<Product> findAllByBrandAndIsPublishedTrue(Brand brand);

    Optional<Product> findBySlugAndIsPublishedTrue(String slug);

    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:productName% " +
            "AND (p.brand.name IN :brandName OR (:brandName is null OR :brandName = '')) " +
            "AND p.isVisibleIndividually = TRUE " +
            "AND p.isPublished = TRUE " +
            "ORDER BY p.lastModifiedOn DESC")
    Page<Product> getProductsWithFilter(@Param("productName") String productName,
                                        @Param("brandName") String brandName,
                                        Pageable pageable);
    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:productName% " +
            "AND (p.brand.name IN :brandName OR (:brandName is null OR :brandName = '')) " +
            "AND p.isVisibleIndividually = TRUE " +
            "AND p.isPublished = TRUE " +
            "ORDER BY p.lastModifiedOn DESC")
    List<Product> getExportingProducts(@Param("productName") String productName, @Param("brandName") String brandName);
    List<Product> findAllByIdIn(List<Long> productIds);

    Optional<Product> findByGtinAndIsPublishedTrue(String gtin);

    Optional<Product> findBySkuAndIsPublishedTrue(String sku);

    @Query(value = "FROM Product p WHERE p.isFeatured = TRUE " +
            "AND p.isVisibleIndividually = TRUE " +
            "AND p.isPublished = TRUE ORDER BY p.lastModifiedOn DESC")
    Page<Product> getFeaturedProduct(Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN p.productCategories pc LEFT JOIN pc.category c " +
            "WHERE LOWER(p.name) LIKE %:productName% " +
            "AND (c.slug = :categorySlug OR (:categorySlug IS NULL OR :categorySlug = '')) " +
            "AND (:startPrice IS NULL OR p.price >= :startPrice) " +
            "AND (:endPrice IS NULL OR p.price <= :endPrice) " +
            "AND p.isVisibleIndividually = TRUE " +
            "AND p.isPublished = TRUE " +
            "ORDER BY p.lastModifiedOn DESC")
    Page<Product> findByProductNameAndCategorySlugAndPriceBetween(@Param("productName") String productName,
                                                                  @Param("categorySlug") String categorySlug,
                                                                  @Param("startPrice") Double startPrice,
                                                                  @Param("endPrice") Double endPrice,
                                                                  Pageable pageable);
}
