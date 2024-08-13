package com.yas.location.repository;

import com.yas.location.model.District;
import com.yas.location.viewmodel.district.DistrictGetVm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<DistrictGetVm> findAllByStateProvinceIdOrderByNameAsc(Long stateProvinceId);
}
