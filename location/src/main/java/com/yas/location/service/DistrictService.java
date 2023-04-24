package com.yas.location.service;

import com.yas.location.repository.DistrictRepository;
import com.yas.location.viewmodel.district.DistrictGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepository;
    public List<DistrictGetVm> getList(Long id) {
        return districtRepository.findAllByStateProvinceIdOrderByNameAsc(id);
    }
}
