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
    @Query(value = "from Product p where p.name = :productName and p.brand.name = :brandName ")
    Page<Product> getProductsWithFilter(@Param("productName") String productName,
                                        @Param("brandName") String brandName,
                                        Pageable pageable);

    Page<Product> findByName(Pageable pageable, String productName);
    Page<Product> findByBrandName(Pageable pageable, String brandName);

//    Page<Product> findByIsPublishedTrueAndIsVisibleIndividuallyTrue(Pageable pageable);

    @Query(value = "select * from product p where p.id  = p.parent_id and p.is_published  and p.is_visible_individually", nativeQuery = true)
    Page<Product> getFeaturedProduct(Pageable pageable);
}
