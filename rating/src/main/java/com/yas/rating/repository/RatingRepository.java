package com.yas.rating.repository;

import com.yas.rating.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByProductId(Long id, Pageable pageable);

    @Query(value = "SELECT r FROM Rating r " +
            "Where (LOWER(r.productName) LIKE %:productName%) " +
            "AND CONCAT(LOWER(r.firstName), ' ', LOWER(r.lastName)) LIKE %:customerName% " +
            "AND LOWER(r.content) LIKE %:message% " +
            "AND r.createdOn BETWEEN :createdFrom AND :createdTo")
    Page<Rating> getRatingListWithFilter(
            @Param("productName") String productName,
            @Param("customerName") String customerName,
            @Param("message") String message,
            @Param("createdFrom") ZonedDateTime createdFrom,
            @Param("createdTo") ZonedDateTime createdTo,
            Pageable pageable);

    @Query(value = "SELECT SUM(r.ratingStar), COUNT(r) FROM Rating r Where r.productId = :productId")
    List<Object[]> getTotalStarsAndTotalRatings(@Param("productId") long productId);

    boolean existsByCreatedByAndProductId(String createdBy, Long productId);
}