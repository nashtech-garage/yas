package com.yas.location.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.LocationApplication;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
class StateOrProvinceMapperTest {

    @Autowired
    private StateOrProvinceMapper stateOrProvinceMapper;

    @Test
    void testToStateOrProvinceViewModelFromStateOrProvince_Success() {
        Country country = Country.builder()
            .id(1L)
            .name("United States")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .code("CA")
            .name("California")
            .type("State")
            .country(country)
            .build();

        StateOrProvinceVm vm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("CA", vm.code());
        assertEquals("California", vm.name());
        assertEquals("State", vm.type());
        assertEquals(1L, vm.countryId());
    }

    @Test
    void testToStateOrProvinceViewModelWithNullType_Success() {
        Country country = Country.builder()
            .id(2L)
            .name("Canada")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(2L)
            .code("ON")
            .name("Ontario")
            .country(country)
            .build();

        StateOrProvinceVm vm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertNotNull(vm);
        assertEquals(2L, vm.id());
        assertEquals("ON", vm.code());
        assertEquals("Ontario", vm.name());
        assertEquals(2L, vm.countryId());
    }

    @Test
    void testToStateOrProvinceViewModelMultipleInstances_Success() {
        Country country = Country.builder().id(1L).name("Country").build();

        StateOrProvince state1 = StateOrProvince.builder()
            .id(1L)
            .code("ST1")
            .name("State 1")
            .type("State")
            .country(country)
            .build();

        StateOrProvince state2 = StateOrProvince.builder()
            .id(2L)
            .code("ST2")
            .name("State 2")
            .type("State")
            .country(country)
            .build();

        StateOrProvinceVm vm1 = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(state1);
        StateOrProvinceVm vm2 = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(state2);

        assertNotNull(vm1);
        assertNotNull(vm2);
        assertEquals(1L, vm1.id());
        assertEquals(2L, vm2.id());
        assertEquals("ST1", vm1.code());
        assertEquals("ST2", vm2.code());
    }

    @Test
    void testToStateOrProvinceViewModelWithNullCode_Success() {
        Country country = Country.builder().id(1L).name("Country").build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("Province")
            .country(country)
            .build();

        StateOrProvinceVm vm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Province", vm.name());
        assertEquals(1L, vm.countryId());
    }

    @Test
    void testMapperMappingCountryId_Success() {
        Country country = Country.builder()
            .id(999L)
            .name("Test Country")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("Test Province")
            .code("TP")
            .country(country)
            .build();

        StateOrProvinceVm vm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertEquals(999L, vm.countryId());
    }

    @Test
    void testMapperPreservesAllFields_Success() {
        Country country = Country.builder().id(1L).name("Country").build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(123L)
            .code("CODE123")
            .name("Full Name")
            .type("Territory")
            .country(country)
            .build();

        StateOrProvinceVm vm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertEquals(123L, vm.id());
        assertEquals("CODE123", vm.code());
        assertEquals("Full Name", vm.name());
        assertEquals("Territory", vm.type());
        assertEquals(1L, vm.countryId());
    }
}
