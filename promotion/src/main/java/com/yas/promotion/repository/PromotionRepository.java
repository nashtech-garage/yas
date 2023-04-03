package com.yas.promotion.repository;

import com.yas.promotion.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findBySlugAndIsActiveTrue(String slug);

    @Query("SELECT p FROM Promotion p "+
            "WHERE p.name LIKE %:name% "+
            "AND p.couponCode LIKE %:couponCode% "+
            "AND p.startDate >= :startDate "+
            "AND p.endDate <= :endDate")
    Page<Promotion> findPromotions(@Param("name") String name,
                                   @Param("couponCode") String couponCode,
                                   @Param("startDate") ZonedDateTime startDate,
                                   @Param("endDate") ZonedDateTime endDate,
                                   Pageable pageable);

}
