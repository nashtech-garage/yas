package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class OrderAddressTest {

    @Test
    void testOrderAddressBuilder_shouldCreateValidOrderAddress() {
        OrderAddress address = OrderAddress.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("+1234567890")
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("Springfield")
                .zipCode("62701")
                .districtId(101L)
                .districtName("Downtown")
                .stateOrProvinceId(201L)
                .stateOrProvinceName("Illinois")
                .countryId(301L)
                .countryName("USA")
                .build();

        assertNotNull(address);
        assertEquals(1L, address.getId());
        assertEquals("John Doe", address.getContactName());
        assertEquals("+1234567890", address.getPhone());
        assertEquals("123 Main St", address.getAddressLine1());
        assertEquals("Apt 4B", address.getAddressLine2());
        assertEquals("Springfield", address.getCity());
        assertEquals("62701", address.getZipCode());
        assertEquals(101L, address.getDistrictId());
        assertEquals("Downtown", address.getDistrictName());
        assertEquals(201L, address.getStateOrProvinceId());
        assertEquals("Illinois", address.getStateOrProvinceName());
        assertEquals(301L, address.getCountryId());
        assertEquals("USA", address.getCountryName());
    }

    @Test
    void testOrderAddressNoArgsConstructor_shouldCreateEmptyAddress() {
        OrderAddress address = new OrderAddress();

        assertNotNull(address);
        assertNull(address.getId());
        assertNull(address.getContactName());
    }

    @Test
    void testOrderAddressSetters_shouldUpdateFields() {
        OrderAddress address = new OrderAddress();

        address.setId(2L);
        address.setContactName("Jane Smith");
        address.setPhone("+9876543210");
        address.setAddressLine1("456 Oak Ave");
        address.setCity("Metropolis");
        address.setZipCode("12345");
        address.setCountryId(100L);
        address.setCountryName("United States");

        assertEquals(2L, address.getId());
        assertEquals("Jane Smith", address.getContactName());
        assertEquals("+9876543210", address.getPhone());
        assertEquals("456 Oak Ave", address.getAddressLine1());
        assertEquals("Metropolis", address.getCity());
        assertEquals("12345", address.getZipCode());
        assertEquals(100L, address.getCountryId());
        assertEquals("United States", address.getCountryName());
    }

    @Test
    void testOrderAddressAllArgsConstructor_shouldCreateValidAddress() {
        OrderAddress address = new OrderAddress(
                1L, "Contact", "Phone", "Line1", "Line2",
                "City", "Zip", 1L, "District",
                2L, "State", 3L, "Country"
        );

        assertNotNull(address);
        assertEquals(1L, address.getId());
        assertEquals("Contact", address.getContactName());
    }
}
