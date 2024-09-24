package com.yas.promotion.repository;

import com.yas.promotion.model.Promotion;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findBySlugAndIsActiveTrue(String slug);

    @Query("SELECT p FROM Promotion p "
            + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')) "
            + "AND LOWER(p.couponCode) LIKE LOWER(CONCAT('%',:couponCode,'%')) "
            + "AND (cast(:startDate as date) is null or (p.startDate BETWEEN :startDate AND :endDate)) "
            + "AND (cast(:startDate as date) is null or (p.endDate BETWEEN :startDate AND :endDate))")
    Page<Promotion> findPromotions(@Param("name") String name,
                                   @Param("couponCode") String couponCode,
                                   @Param("startDate") Instant startDate,
                                   @Param("endDate") Instant endDate,
                                   Pageable pageable);

}
