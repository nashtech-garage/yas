package com.yas.location.repository;

import com.yas.location.model.StateOrProvince;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateOrProvinceRepository extends JpaRepository<StateOrProvince, Long> {

  boolean existsById(final Long id);

  boolean existsByName(final String name);

  @Query("""
       SELECT CASE
                 WHEN count(1)> 0 THEN TRUE
                 ELSE FALSE
              END
       FROM StateOrProvince c
       WHERE c.name = ?1
       AND c.id != ?2
      """)
  boolean existsByNameNotUpdatingStateOrProvince(final String name, final Long id);

  @Query(value = """
       SELECT sop
       FROM StateOrProvince sop
       WHERE sop.country.id = :countryId
       ORDER BY sop.lastModifiedOn DESC
      """)
  Page<StateOrProvince>  getPageableStateOrProvincesByCountry(@Param("countryId") final Long countryId,
                                                             final Pageable pageable);
  List<StateOrProvince> findByIdIn(@Param("stateOrProvinceIds") final List<Long> stateOrProvinceIds);

  @Query(value = """
       SELECT sop
       FROM StateOrProvince sop
       WHERE sop.country.id = :countryId
       ORDER BY sop.lastModifiedOn DESC
      """)
  List<StateOrProvince> getStateOrProvincesByCountry(@Param("countryId") final Long countryId);

}
