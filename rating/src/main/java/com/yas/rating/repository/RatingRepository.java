package com.yas.rating.repository;

import com.yas.rating.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByProductId(Long id, Pageable pageable);

    @Query(value = "SELECT r FROM Rating r " +
            "Where r.productId = :productId " +
            "AND CONCAT(LOWER(r.firstName), ' ', LOWER(r.lastName)) LIKE %:name%")
    Page<Rating> findByProductIdAndCustomerName(@Param("productId") Long id,@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT SUM(r.ratingStar), COUNT(r) FROM Rating r Where r.productId = :productId")
    List<Object[]> getTotalStarsAndTotalRatings(@Param("productId") long productId);
}