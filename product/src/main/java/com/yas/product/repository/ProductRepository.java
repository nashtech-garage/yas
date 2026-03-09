package com.yas.product.repository;

import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByBrandAndIsPublishedTrueOrderByIdAsc(Brand brand);

    Optional<Product> findBySlugAndIsPublishedTrue(String slug);

    Optional<Product> findByGtinAndIsPublishedTrue(String gtin);

    Optional<Product> findBySkuAndIsPublishedTrue(String sku);

    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:productName% "
            + "AND (p.brand.name IN :brandName OR (:brandName is null OR :brandName = '')) "
            + "AND p.isVisibleIndividually = TRUE "
            + "AND p.isPublished = TRUE "
            + "ORDER BY p.id ASC ")
    Page<Product> getProductsWithFilter(@Param("productName") String productName,
                                        @Param("brandName") String brandName,
                                        Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:productName% "
            + "AND (p.brand.name IN :brandName OR (:brandName is null OR :brandName = '')) "
            + "AND p.isVisibleIndividually = TRUE "
            + "AND p.isPublished = TRUE "
            + "ORDER BY p.id ASC ")
    List<Product> getExportingProducts(@Param("productName") String productName, @Param("brandName") String brandName);

    List<Product> findAllByIdIn(List<Long> productIds);

    @Query(value = "FROM Product p WHERE p.isFeatured = TRUE "
            + "AND p.isVisibleIndividually = TRUE "
            + "AND p.isPublished = TRUE ORDER BY p.id ASC ")
    Page<Product> getFeaturedProduct(Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN p.productCategories pc LEFT JOIN pc.category c "
            + "WHERE LOWER(p.name) LIKE %:productName% "
            + "AND (c.slug = :categorySlug OR (:categorySlug IS NULL OR :categorySlug = '')) "
            + "AND (:startPrice IS NULL OR p.price >= :startPrice) "
            + "AND (:endPrice IS NULL OR p.price <= :endPrice) "
            + "AND p.isVisibleIndividually = TRUE "
            + "AND p.isPublished = TRUE "
            + "ORDER BY p.id ASC ")
    Page<Product> findByProductNameAndCategorySlugAndPriceBetween(@Param("productName") String productName,
                                                                  @Param("categorySlug") String categorySlug,
                                                                  @Param("startPrice") Double startPrice,
                                                                  @Param("endPrice") Double endPrice,
                                                                  Pageable pageable);

    @Query(value = "SELECT p FROM Product p "
            + "WHERE (LOWER(p.name) LIKE concat('%', LOWER(:name), '%') "
            + "OR LOWER(p.sku) LIKE concat('%', LOWER(:sku), '%')) "
            + "AND ((:selection = 'ALL') "
            + "OR ((:selection = 'YES' and p.id in :productIds ) "
            + "OR (:selection = 'NO' and ((coalesce(:productIds) is null) or p.id not in :productIds)))) "
            + "ORDER BY p.id ASC ")
    List<Product> findProductForWarehouse(@Param("name") String name, @Param("sku") String sku,
                                          @Param("productIds") List<Long> productIds,
                                          @Param("selection") String selection);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.productCategories pc WHERE pc.id IN :categoryIds ORDER BY p.id ASC")
    List<Product> findByCategoryIdsIn(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT p FROM Product p JOIN p.brand b WHERE b.id IN :brandIds ORDER BY p.id ASC")
    List<Product> findByBrandIdsIn(@Param("brandIds") List<Long> brandIds);

    @Query("SELECT p FROM Product p ORDER BY p.createdOn DESC, p.id DESC")
    List<Product> getLatestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id IN :productIds AND p.isPublished = TRUE")
    Page<Product> findAllPublishedProductsByIds(@Param("productIds") List<Long> productIds, Pageable pageable);
}