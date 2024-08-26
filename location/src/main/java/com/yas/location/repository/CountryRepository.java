package com.yas.location.repository;

import com.yas.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCode2IgnoreCase(String code2);

    boolean existsByCode2IgnoreCaseAndIdNot(String code2, Long id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
