package com.yas.location.repository;

import com.yas.location.model.District;
import com.yas.location.viewmodel.district.DistrictGetVm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<DistrictGetVm> findAllByStateProvinceIdOrderByNameAsc(Long stateProvinceId);
}
