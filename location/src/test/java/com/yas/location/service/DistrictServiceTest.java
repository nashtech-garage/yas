package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.location.repository.DistrictRepository;
import com.yas.location.viewmodel.district.DistrictGetVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DistrictServiceTest {
    private DistrictRepository districtRepository;
    private DistrictService districtService;

    @BeforeEach
    void setUp() {
        districtRepository = mock(DistrictRepository.class);
        districtService = new DistrictService(districtRepository);
    }

    @Test
    void getList_whenCalled_returnList() {
        DistrictGetVm vm = new DistrictGetVm(1L, "District");
        when(districtRepository.findAllByStateProvinceIdOrderByNameAsc(1L)).thenReturn(List.of(vm));

        List<DistrictGetVm> result = districtService.getList(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("District");
    }
}
