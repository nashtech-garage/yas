package com.yas.location.repository;

import com.yas.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByName(String name);
    @Query("select case when count(1)> 0 then true else false end from Country c where c.name = ?1 and c.id != ?2")
    boolean existsByNameNotUpdatingCountry(String name, Long id);
}
