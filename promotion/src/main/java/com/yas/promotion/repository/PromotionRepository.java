package com.yas.promotion.repository;

import com.yas.promotion.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findBySlugAndIsActiveTrue(String slug);

//    @Query("select p from Promotion p where p.name = :name and (:id is null or p.id != :id)")
//    Promotion findExistedName(String name, Long id);
}
