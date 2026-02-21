package com.yas.location.viewmodel.stateorprovince;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import org.junit.jupiter.api.Test;

class StateOrProvinceVmTest {

    @Test
    void testStateOrProvinceVmFromModel_Success() {
        Country country = Country.builder()
            .id(1L)
            .name("Test Country")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .code("CA")
            .name("California")
            .type("State")
            .country(country)
            .build();

        StateOrProvinceVm vm = StateOrProvinceVm.fromModel(stateOrProvince);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("CA", vm.code());
        assertEquals("California", vm.name());
        assertEquals("State", vm.type());
        assertEquals(1L, vm.countryId());
    }

    @Test
    void testStateOrProvinceVmRecord_Success() {
        StateOrProvinceVm vm = new StateOrProvinceVm(2L, "New York", "NY", "State", 1L);

        assertNotNull(vm);
        assertEquals(2L, vm.id());
        assertEquals("NY", vm.code());
        assertEquals("New York", vm.name());
    }

    @Test
    void testStateOrProvincePostVmBuilder_Success() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder()
            .countryId(1L)
            .name("Texas")
            .code("TX")
            .type("State")
            .build();

        assertNotNull(postVm);
        assertEquals(1L, postVm.countryId());
        assertEquals("Texas", postVm.name());
        assertEquals("TX", postVm.code());
        assertEquals("State", postVm.type());
    }

    @Test
    void testStateOrProvinceVmFromModelNullType_Success() {
        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("Province")
            .country(country)
            .build();

        StateOrProvinceVm vm = StateOrProvinceVm.fromModel(stateOrProvince);

        assertEquals(1L, vm.id());
        assertEquals("Province", vm.name());
        assertEquals(1L, vm.countryId());
    }

    @Test
    void testStateOrProvinceAndCountryGetNameVm_Success() {
        Country country = Country.builder()
            .id(1L)
            .name("Canada")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(2L)
            .name("Ontario")
            .country(country)
            .build();

        StateOrProvinceAndCountryGetNameVm vm = StateOrProvinceAndCountryGetNameVm.fromModel(stateOrProvince);

        assertNotNull(vm);
        assertEquals(2L, vm.stateOrProvinceId());
        assertEquals("Ontario", vm.stateOrProvinceName());
        assertEquals("Canada", vm.countryName());
    }

    @Test
    void testStateOrProvinceListGetVm_Success() {
        StateOrProvinceVm vm1 = new StateOrProvinceVm(1L, "State1", "S1", "Type", 1L);
        StateOrProvinceVm vm2 = new StateOrProvinceVm(2L, "State2", "S2", "Type", 1L);

        StateOrProvinceListGetVm listVm = new StateOrProvinceListGetVm(
            java.util.List.of(vm1, vm2), 0, 10, 2, 1, true
        );

        assertNotNull(listVm);
        assertEquals(2, listVm.stateOrProvinceContent().size());
        assertEquals(0, listVm.pageNo());
        assertEquals(10, listVm.pageSize());
        assertEquals(2, listVm.totalElements());
    }
}
