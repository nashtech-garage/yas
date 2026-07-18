package com.yas.location.viewmodel.stateorprovince;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import org.junit.jupiter.api.Test;

class StateOrProvinceVmTest {

    @Test
    void fromModel_mapsFields() {
        Country country = Country.builder().id(3L).name("C").build();
        StateOrProvince sop = StateOrProvince.builder()
            .id(9L)
            .name("State name")
            .code("ST")
            .type("province")
            .country(country)
            .build();

        StateOrProvinceVm vm = StateOrProvinceVm.fromModel(sop);

        assertEquals(9L, vm.id());
        assertEquals("State name", vm.name());
        assertEquals("ST", vm.code());
        assertEquals("province", vm.type());
        assertEquals(3L, vm.countryId());
    }
}
