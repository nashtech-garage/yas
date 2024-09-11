package com.yas.product.repository;

import com.yas.product.model.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Modifying
    @Query("DELETE FROM ProductImage p WHERE p.product.id = :productId AND p.imageId IN :imageIds")
    void deleteByImageIdInAndProductId(List<Long> imageIds, Long productId);
}
