package com.yas.location.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StateOrProvinceTest {

    @Test
    void testStateOrProvinceBuilder_Success() {
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

        assertNotNull(stateOrProvince);
        assertEquals(1L, stateOrProvince.getId());
        assertEquals("CA", stateOrProvince.getCode());
        assertEquals("California", stateOrProvince.getName());
        assertEquals("State", stateOrProvince.getType());
        assertEquals(country, stateOrProvince.getCountry());
    }

    @Test
    void testStateOrProvinceNoArgsConstructor_Success() {
        StateOrProvince stateOrProvince = new StateOrProvince();
        assertNotNull(stateOrProvince);
    }

    @Test
    void testStateOrProvinceSetters_Success() {
        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince stateOrProvince = new StateOrProvince();

        stateOrProvince.setId(2L);
        stateOrProvince.setCode("NY");
        stateOrProvince.setName("New York");
        stateOrProvince.setType("State");
        stateOrProvince.setCountry(country);

        assertEquals(2L, stateOrProvince.getId());
        assertEquals("NY", stateOrProvince.getCode());
        assertEquals("New York", stateOrProvince.getName());
        assertEquals("State", stateOrProvince.getType());
        assertEquals(country, stateOrProvince.getCountry());
    }

    @Test
    void testStateOrProvinceGetters_Success() {
        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince stateOrProvince = new StateOrProvince(1L, "TX", "Texas", "State", country);

        assertEquals(1L, stateOrProvince.getId());
        assertEquals("TX", stateOrProvince.getCode());
        assertEquals("Texas", stateOrProvince.getName());
        assertEquals("State", stateOrProvince.getType());
        assertEquals(country, stateOrProvince.getCountry());
    }

    @Test
    void testStateOrProvinceNullValues_Success() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("Test State")
            .build();

        assertEquals(1L, stateOrProvince.getId());
        assertEquals("Test State", stateOrProvince.getName());
    }

    @Test
    void testStateOrProvinceAllArgsConstructor_Success() {
        Country country = Country.builder().id(1L).build();
        StateOrProvince stateOrProvince = new StateOrProvince(1L, "FL", "Florida", "State", country);

        assertNotNull(stateOrProvince);
        assertEquals(1L, stateOrProvince.getId());
        assertEquals("FL", stateOrProvince.getCode());
    }
}
