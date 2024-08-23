package com.yas.location.repository;

import com.yas.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCode2IgnoreCase(String code2);

    @Query("""
         SELECT CASE
                   WHEN count(1)> 0 THEN TRUE
                   ELSE FALSE
                END
         FROM Country c
         WHERE LOWER(c.name) = LOWER(?1)
         AND c.id != ?2
        """)
    boolean existsByNameNotUpdatingCountry(String name, Long id);

    @Query("""
         SELECT CASE
                   WHEN count(1)> 0 THEN TRUE
                   ELSE FALSE
                END
         FROM Country c
         WHERE LOWER(c.code2) = LOWER(?1)
         AND c.id != ?2
        """)
    boolean existsByCode2NotUpdatingCountry(String code, Long id);
}
