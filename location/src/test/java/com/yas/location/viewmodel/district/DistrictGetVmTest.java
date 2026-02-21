package com.yas.location.viewmodel.district;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DistrictGetVmTest {

    @Test
    void testDistrictGetVm_Success() {
        DistrictGetVm vm = new DistrictGetVm(1L, "Los Angeles County");

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Los Angeles County", vm.name());
    }

    @Test
    void testDistrictGetVm_NullName_Success() {
        DistrictGetVm vm = new DistrictGetVm(1L, null);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
    }

    @Test
    void testDistrictGetVm_EmptyName_Success() {
        DistrictGetVm vm = new DistrictGetVm(1L, "");

        assertNotNull(vm);
        assertEquals("", vm.name());
    }

    @Test
    void testDistrictGetVm_LongName_Success() {
        String longName = "A".repeat(450);
        DistrictGetVm vm = new DistrictGetVm(2L, longName);

        assertNotNull(vm);
        assertEquals(longName, vm.name());
    }

    @Test
    void testDistrictGetVm_MultipleInstances_Success() {
        DistrictGetVm vm1 = new DistrictGetVm(1L, "District 1");
        DistrictGetVm vm2 = new DistrictGetVm(2L, "District 2");
        DistrictGetVm vm3 = new DistrictGetVm(3L, "District 3");

        assertNotNull(vm1);
        assertNotNull(vm2);
        assertNotNull(vm3);

        assertEquals(1L, vm1.id());
        assertEquals(2L, vm2.id());
        assertEquals(3L, vm3.id());
    }

    @Test
    void testDistrictGetVm_SpecialCharactersName_Success() {
        String specialName = "District #@!$%^&*()";
        DistrictGetVm vm = new DistrictGetVm(1L, specialName);

        assertEquals(specialName, vm.name());
    }

    @Test
    void testDistrictGetVm_UnicodeCharacters_Success() {
        String unicodeName = "區域 地區 区域";
        DistrictGetVm vm = new DistrictGetVm(1L, unicodeName);

        assertEquals(unicodeName, vm.name());
    }
}
