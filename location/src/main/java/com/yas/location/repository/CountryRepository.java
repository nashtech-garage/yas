package com.yas.location.repository;

import com.yas.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

//    @Query("select c from country c where c.name = ?1 and (?2 is null or c.id != ?2)")
//    Country findExistedName(String name, Long id);

    @Query("select case when count(c)> 0 then true else false end from Country c where c.name = ?1 and (?2 is null or c.id != ?2)")
    boolean existsCountryName(String name, Long id);
}
