package com.yas.product.repository;

import com.yas.product.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author toaitrano
 * @version 1.0
 * @since 2022/06/18
 */

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {

}
