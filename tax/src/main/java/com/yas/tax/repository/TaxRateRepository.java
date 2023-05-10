package com.yas.tax.repository;

import com.yas.tax.model.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

    @Query(value = "SELECT rate FROM tax_rate tr WHERE tr.country_id = :countryId " +
            "AND (tr.state_or_province_id = :stateOrProvinceId OR  tr.state_or_province_id is null) " +
            "AND tr.tax_class_id = :taxClassId " +
            "AND (tr.zip_code = :zipCode OR  trim(tr.zip_code) = '' OR  tr.zip_code is null ) " +
            "FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
    Double getTaxPercent(@Param("countryId") Long countryId, @Param("stateOrProvinceId") Long stateOrProvinceId, @Param("zipCode") String zipCode, @Param("taxClassId") Long taxClassId);
}
