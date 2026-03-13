package com.yas.order.viewmodel.orderaddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.order.model.OrderAddress;
import org.junit.jupiter.api.Test;

class OrderAddressVmTest {

    @Test
    void fromModel_shouldMapAllFields() {
        OrderAddress address = OrderAddress.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("+123456789")
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

        OrderAddressVm result = OrderAddressVm.fromModel(address);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Doe", result.contactName());
        assertEquals("+123456789", result.phone());
        assertEquals("123 Main St", result.addressLine1());
        assertEquals("Apt 4B", result.addressLine2());
        assertEquals("Springfield", result.city());
        assertEquals("62701", result.zipCode());
        assertEquals(101L, result.districtId());
        assertEquals("Downtown", result.districtName());
        assertEquals(201L, result.stateOrProvinceId());
        assertEquals("Illinois", result.stateOrProvinceName());
        assertEquals(301L, result.countryId());
        assertEquals("USA", result.countryName());
    }

    @Test
    void fromModel_whenOptionalFieldsNull_shouldMapNulls() {
        OrderAddress address = OrderAddress.builder()
                .id(1L)
                .contactName("Jane")
                .phone("123")
                .addressLine1("Addr")
                .build();

        OrderAddressVm result = OrderAddressVm.fromModel(address);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.addressLine2());
        assertNull(result.city());
        assertNull(result.zipCode());
    }

    @Test
    void builder_shouldCreateValidVm() {
        OrderAddressVm vm = OrderAddressVm.builder()
                .id(5L)
                .contactName("Builder Test")
                .phone("+999")
                .addressLine1("Builder Addr")
                .city("Builder City")
                .countryName("Builder Country")
                .build();

        assertNotNull(vm);
        assertEquals(5L, vm.id());
        assertEquals("Builder Test", vm.contactName());
        assertEquals("+999", vm.phone());
    }
}
