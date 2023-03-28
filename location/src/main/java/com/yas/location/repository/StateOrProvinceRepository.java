package com.yas.location.repository;

import com.yas.location.model.StateOrProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Repository
public interface StateOrProvinceRepository extends JpaRepository<StateOrProvince, Long> {

    boolean existsByName(String name);
    @Query("select case when count(1)> 0 then true else false end from StateOrProvince c where c.name = ?1 and c.id != ?2")
    boolean existsByNameNotUpdatingStateOrProvince(String name, Long id);

    @Query(value = "SELECT sop FROM StateOrProvince sop WHERE sop.country.id = :countryId " + "ORDER BY sop.lastModifiedOn DESC")
    Page<StateOrProvince> getStateOrProvinceByCountry(@Param("countryId") Long countryId, Pageable pageable);
}