package com.yas.order.viewmodel.orderaddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderAddressPostVmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testOrderAddressPostVm_withValidData_shouldHaveNoViolations() {
        OrderAddressPostVm vm = buildValidAddress();

        Set<ConstraintViolation<OrderAddressPostVm>> violations = validator.validate(vm);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testOrderAddressPostVm_whenContactNameIsBlank_shouldHaveViolation() {
        OrderAddressPostVm vm = OrderAddressPostVm.builder()
                .contactName("")
                .phone("+123")
                .addressLine1("Address")
                .city("City")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();

        Set<ConstraintViolation<OrderAddressPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("contactName")));
    }

    @Test
    void testOrderAddressPostVm_whenPhoneIsBlank_shouldHaveViolation() {
        OrderAddressPostVm vm = OrderAddressPostVm.builder()
                .contactName("Contact")
                .phone("")
                .addressLine1("Address")
                .city("City")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();

        Set<ConstraintViolation<OrderAddressPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    void testOrderAddressPostVm_whenDistrictIdIsNull_shouldHaveViolation() {
        OrderAddressPostVm vm = OrderAddressPostVm.builder()
                .contactName("Contact")
                .phone("123")
                .addressLine1("Address")
                .city("City")
                .zipCode("12345")
                .districtId(null)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();

        Set<ConstraintViolation<OrderAddressPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("districtId")));
    }

    @Test
    void testOrderAddressPostVm_whenCountryIdIsNull_shouldHaveViolation() {
        OrderAddressPostVm vm = OrderAddressPostVm.builder()
                .contactName("Contact")
                .phone("123")
                .addressLine1("Address")
                .city("City")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(null)
                .countryName("Country")
                .build();

        Set<ConstraintViolation<OrderAddressPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("countryId")));
    }

    @Test
    void testOrderAddressPostVm_recordAccessors_shouldReturnCorrectValues() {
        OrderAddressPostVm vm = buildValidAddress();

        assertEquals("John Doe", vm.contactName());
        assertEquals("+123456789", vm.phone());
        assertEquals("123 Main St", vm.addressLine1());
        assertEquals("Apt 4B", vm.addressLine2());
        assertEquals("Springfield", vm.city());
        assertEquals("62701", vm.zipCode());
        assertEquals(101L, vm.districtId());
        assertEquals("Downtown", vm.districtName());
        assertEquals(201L, vm.stateOrProvinceId());
        assertEquals("Illinois", vm.stateOrProvinceName());
        assertEquals(301L, vm.countryId());
        assertEquals("USA", vm.countryName());
    }

    private OrderAddressPostVm buildValidAddress() {
        return OrderAddressPostVm.builder()
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
    }
}
