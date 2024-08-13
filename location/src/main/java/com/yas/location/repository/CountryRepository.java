package com.yas.location.repository;

import com.yas.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByName(String name);

    @Query("""
         SELECT CASE
                   WHEN count(1)> 0 THEN TRUE
                   ELSE FALSE
                END
         FROM Country c
         WHERE c.name = ?1
         AND c.id != ?2
        """)
    boolean existsByNameNotUpdatingCountry(String name, Long id);
}
