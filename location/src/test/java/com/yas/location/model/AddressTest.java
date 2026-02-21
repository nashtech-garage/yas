package com.yas.location.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void testAddressBuilder_Success() {
        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince stateOrProvince = StateOrProvince.builder().id(1L).name("State").build();
        District district = District.builder().id(1L).name("District").build();

        Address address = Address.builder()
            .id(1L)
            .contactName("John Doe")
            .phone("1234567890")
            .addressLine1("123 Main St")
            .addressLine2("Apt 4B")
            .city("New York")
            .zipCode("10001")
            .district(district)
            .stateOrProvince(stateOrProvince)
            .country(country)
            .build();

        assertNotNull(address);
        assertEquals(1L, address.getId());
        assertEquals("John Doe", address.getContactName());
        assertEquals("1234567890", address.getPhone());
        assertEquals("123 Main St", address.getAddressLine1());
        assertEquals("Apt 4B", address.getAddressLine2());
        assertEquals("New York", address.getCity());
        assertEquals("10001", address.getZipCode());
        assertEquals(district, address.getDistrict());
        assertEquals(stateOrProvince, address.getStateOrProvince());
        assertEquals(country, address.getCountry());
    }

    @Test
    void testAddressNoArgsConstructor_Success() {
        Address address = new Address();
        assertNotNull(address);
    }

    @Test
    void testAddressSetters_Success() {
        Country country = Country.builder().id(1L).build();
        StateOrProvince state = StateOrProvince.builder().id(1L).build();
        District district = District.builder().id(1L).build();

        Address address = new Address();
        address.setId(2L);
        address.setContactName("Jane Smith");
        address.setPhone("9876543210");
        address.setAddressLine1("456 Oak St");
        address.setAddressLine2("Suite 100");
        address.setCity("Los Angeles");
        address.setZipCode("90001");
        address.setDistrict(district);
        address.setStateOrProvince(state);
        address.setCountry(country);

        assertEquals(2L, address.getId());
        assertEquals("Jane Smith", address.getContactName());
        assertEquals("9876543210", address.getPhone());
        assertEquals("456 Oak St", address.getAddressLine1());
        assertEquals("Suite 100", address.getAddressLine2());
        assertEquals("Los Angeles", address.getCity());
        assertEquals("90001", address.getZipCode());
        assertEquals(district, address.getDistrict());
        assertEquals(state, address.getStateOrProvince());
        assertEquals(country, address.getCountry());
    }

    @Test
    void testAddressGetters_Success() {
        Country country = Country.builder().id(1L).build();
        StateOrProvince state = StateOrProvince.builder().id(1L).build();
        District district = District.builder().id(1L).build();

        Address address = new Address(1L, "Contact", "123456", "Line1", "Line2", "City", "12345", district,
            state, country);

        assertEquals(1L, address.getId());
        assertEquals("Contact", address.getContactName());
        assertEquals("123456", address.getPhone());
        assertEquals("Line1", address.getAddressLine1());
        assertEquals("Line2", address.getAddressLine2());
        assertEquals("City", address.getCity());
        assertEquals("12345", address.getZipCode());
        assertEquals(district, address.getDistrict());
        assertEquals(state, address.getStateOrProvince());
        assertEquals(country, address.getCountry());
    }

    @Test
    void testAddressAllArgsConstructor_Success() {
        Country country = Country.builder().id(1L).build();
        StateOrProvince state = StateOrProvince.builder().id(1L).build();
        District district = District.builder().id(1L).build();

        Address address = new Address(1L, "Name", "Phone", "Line1", "Line2", "City", "Zip", district,
            state, country);

        assertNotNull(address);
        assertEquals(1L, address.getId());
        assertEquals("Name", address.getContactName());
    }

    @Test
    void testAddressNullValues_Success() {
        Address address = Address.builder()
            .id(1L)
            .city("Test City")
            .build();

        assertEquals(1L, address.getId());
        assertEquals("Test City", address.getCity());
    }

    @Test
    void testAddressPartialData_Success() {
        Address address = Address.builder()
            .id(1L)
            .contactName("John")
            .addressLine1("123 Main")
            .city("Boston")
            .build();

        assertNotNull(address);
        assertEquals("John", address.getContactName());
        assertEquals("123 Main", address.getAddressLine1());
        assertEquals("Boston", address.getCity());
    }
}
