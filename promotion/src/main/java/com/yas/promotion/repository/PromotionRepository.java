package com.yas.promotion.repository;

import com.yas.promotion.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("select p from Promotion p where p.name = :name and (:id is null or p.id != :id)")
    Promotion findExistedName(String name, Long id);
}
