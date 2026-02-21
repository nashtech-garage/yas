package com.yas.location.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DistrictTest {

    @Test
    void testDistrictBuilder_Success() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("California")
            .build();

        District district = District.builder()
            .id(1L)
            .name("Los Angeles County")
            .type("County")
            .location("Downtown LA")
            .stateProvince(stateOrProvince)
            .build();

        assertNotNull(district);
        assertEquals(1L, district.getId());
        assertEquals("Los Angeles County", district.getName());
        assertEquals("County", district.getType());
        assertEquals("Downtown LA", district.getLocation());
        assertEquals(stateOrProvince, district.getStateProvince());
    }

    @Test
    void testDistrictNoArgsConstructor_Success() {
        District district = new District();
        assertNotNull(district);
    }

    @Test
    void testDistrictSetters_Success() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("State")
            .build();

        District district = new District();
        district.setId(2L);
        district.setName("District Name");
        district.setType("Type");
        district.setLocation("Location");
        district.setStateProvince(stateOrProvince);

        assertEquals(2L, district.getId());
        assertEquals("District Name", district.getName());
        assertEquals("Type", district.getType());
        assertEquals("Location", district.getLocation());
        assertEquals(stateOrProvince, district.getStateProvince());
    }

    @Test
    void testDistrictGetters_Success() {
        StateOrProvince stateOrProvince = StateOrProvince.builder().id(1L).build();
        District district = new District(1L, "District", "Type", "Location", stateOrProvince);

        assertEquals(1L, district.getId());
        assertEquals("District", district.getName());
        assertEquals("Type", district.getType());
        assertEquals("Location", district.getLocation());
        assertEquals(stateOrProvince, district.getStateProvince());
    }

    @Test
    void testDistrictAllArgsConstructor_Success() {
        StateOrProvince stateOrProvince = StateOrProvince.builder().id(1L).build();
        District district = new District(1L, "New District", "District Type", "City Center", stateOrProvince);

        assertNotNull(district);
        assertEquals(1L, district.getId());
        assertEquals("New District", district.getName());
    }

    @Test
    void testDistrictNullValues_Success() {
        District district = District.builder()
            .id(1L)
            .name("Test District")
            .build();

        assertEquals(1L, district.getId());
        assertEquals("Test District", district.getName());
    }

    @Test
    void testDistrictLongNameField_Success() {
        String longName = "A".repeat(450);
        District district = District.builder()
            .id(1L)
            .name(longName)
            .build();

        assertEquals(longName, district.getName());
    }
}
