package com.yas.location.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StateOrProvinceMapperTest {
    private final StateOrProvinceMapper mapper = Mappers.getMapper(StateOrProvinceMapper.class);

    @Test
    void toStateOrProvinceViewModelFromStateOrProvince_whenCalled_mapCorrect() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
                .id(1L)
                .name("State")
                .code("CODE")
                .type("TYPE")
                .country(Country.builder().id(1L).build())
                .build();

        StateOrProvinceVm vm = mapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("State");
        assertThat(vm.countryId()).isEqualTo(1L);
    }
}
