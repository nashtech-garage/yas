package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.location.model.District;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.viewmodel.district.DistrictGetVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DistrictServiceTest {

    @Mock
    private DistrictRepository districtRepository;

    @InjectMocks
    private DistrictService districtService;

    private District district;
    private DistrictGetVm districtGetVm;

    @BeforeEach
    void setUp() {
        district = District.builder()
            .id(1L)
            .name("district-1")
            .build();
        districtGetVm = new DistrictGetVm(district.getId(), district.getName());
    }

    @Test
    void getList_WhenValidId_ShouldReturnList() {
        when(districtRepository.findAllByStateProvinceIdOrderByNameAsc(1L)).thenReturn(List.of(districtGetVm));

        List<DistrictGetVm> result = districtService.getList(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("district-1");
    }
}
