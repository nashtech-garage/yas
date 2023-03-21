package com.yas.product.repository;

import com.yas.product.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByIdIn(List<Long> ids);

    @Query("select e from ProductOption e where e.name = ?1 and (?2 is null or e.id != ?2)")
    ProductOption findExistedName(String name, Long id);
}
