package com.yas.location.viewmodel.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.model.Address;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import org.junit.jupiter.api.Test;

class AddressVmTest {

    @Test
    void testAddressGetVmFromModel_Success() {
        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince state = StateOrProvince.builder().id(2L).name("State").build();
        District district = District.builder().id(3L).name("District").build();

        Address address = Address.builder()
            .id(1L)
            .contactName("John Doe")
            .phone("1234567890")
            .addressLine1("123 Main St")
            .addressLine2("Apt 4B")
            .city("New York")
            .zipCode("10001")
            .district(district)
            .stateOrProvince(state)
            .country(country)
            .build();

        AddressGetVm vm = AddressGetVm.fromModel(address);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("John Doe", vm.contactName());
        assertEquals("1234567890", vm.phone());
        assertEquals("123 Main St", vm.addressLine1());
        assertEquals("Apt 4B", vm.addressLine2());
        assertEquals("New York", vm.city());
        assertEquals("10001", vm.zipCode());
        assertEquals(3L, vm.districtId());
        assertEquals(2L, vm.stateOrProvinceId());
        assertEquals(1L, vm.countryId());
    }

    @Test
    void testAddressGetVmRecord_Success() {
        AddressGetVm vm = new AddressGetVm(1L, "Contact", "123", "Line1", "Line2", "City", "12345", 1L, 2L, 3L);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Contact", vm.contactName());
        assertEquals("123", vm.phone());
    }

    @Test
    void testAddressDetailVmFromModel_Success() {
        Country country = Country.builder().id(1L).name("United States").build();
        StateOrProvince state = StateOrProvince.builder().id(2L).name("California").build();
        District district = District.builder().id(3L).name("Los Angeles").build();

        Address address = Address.builder()
            .id(1L)
            .contactName("Jane Smith")
            .phone("9876543210")
            .addressLine1("456 Oak Ave")
            .addressLine2("Suite 200")
            .city("Los Angeles")
            .zipCode("90001")
            .district(district)
            .stateOrProvince(state)
            .country(country)
            .build();

        AddressDetailVm vm = AddressDetailVm.fromModel(address);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Jane Smith", vm.contactName());
        assertEquals("9876543210", vm.phone());
        assertEquals("456 Oak Ave", vm.addressLine1());
        assertEquals("Suite 200", vm.addressLine2());
        assertEquals("Los Angeles", vm.city());
        assertEquals("90001", vm.zipCode());
        assertEquals(3L, vm.districtId());
        assertEquals("Los Angeles", vm.districtName());
        assertEquals(2L, vm.stateOrProvinceId());
        assertEquals("California", vm.stateOrProvinceName());
        assertEquals(1L, vm.countryId());
        assertEquals("United States", vm.countryName());
    }

    @Test
    void testAddressDetailVmRecord_Success() {
        AddressDetailVm vm = new AddressDetailVm(1L, "Name", "Phone", "Line1", "Line2", "City", "Zip",
            1L, "District", 2L, "State", 3L, "Country");

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Name", vm.contactName());
        assertEquals("District", vm.districtName());
        assertEquals("State", vm.stateOrProvinceName());
        assertEquals("Country", vm.countryName());
    }

    @Test
    void testAddressPostVmBuilder_Success() {
        AddressPostVm postVm = AddressPostVm.builder()
            .contactName("Contact")
            .phone("Phone")
            .addressLine1("Line1")
            .addressLine2("Line2")
            .city("City")
            .zipCode("Zip")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        assertNotNull(postVm);
        assertEquals("Contact", postVm.contactName());
        assertEquals("Phone", postVm.phone());
        assertEquals(1L, postVm.districtId());
        assertEquals(2L, postVm.stateOrProvinceId());
        assertEquals(3L, postVm.countryId());
    }

    @Test
    void testAddressPostVmFromModel_Success() {
        Address address = Address.builder()
            .id(1L)
            .contactName("Test Contact")
            .phone("Test Phone")
            .addressLine1("Test Line1")
            .addressLine2("Test Line2")
            .city("Test City")
            .zipCode("Test Zip")
            .build();

        Address converted = AddressPostVm.fromModel(AddressPostVm.builder()
            .contactName(address.getContactName())
            .phone(address.getPhone())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .zipCode(address.getZipCode())
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build());

        assertNotNull(converted);
        assertEquals("Test Contact", converted.getContactName());
        assertEquals("Test Phone", converted.getPhone());
    }

    @Test
    void testAddressGetVmNullValues_Success() {
        AddressGetVm vm = new AddressGetVm(1L, null, null, null, null, null, null, 1L, 2L, 3L);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals(1L, vm.districtId());
    }
}
