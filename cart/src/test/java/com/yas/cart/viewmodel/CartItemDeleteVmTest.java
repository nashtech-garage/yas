package com.yas.cart.viewmodel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemDeleteVmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCartItemDeleteVmBuilder_withValidData_shouldCreateSuccessfully() {
        // Given & When
        CartItemDeleteVm vm = CartItemDeleteVm.builder()
            .productId(1L)
            .quantity(5)
            .build();

        // Then
        assertNotNull(vm);
        assertEquals(1L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testCartItemDeleteVm_withAllArgsConstructor_shouldCreateSuccessfully() {
        // Given & When
        CartItemDeleteVm vm = new CartItemDeleteVm(2L, 10);

        // Then
        assertNotNull(vm);
        assertEquals(2L, vm.productId());
        assertEquals(10, vm.quantity());
    }

    @Test
    void testCartItemDeleteVm_whenProductIdIsNull_shouldHaveValidationError() {
        // Given
        CartItemDeleteVm vm = new CartItemDeleteVm(null, 5);

        // When
        Set<ConstraintViolation<CartItemDeleteVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
    }

    @Test
    void testCartItemDeleteVm_whenQuantityIsNull_shouldHaveValidationError() {
        // Given
        CartItemDeleteVm vm = new CartItemDeleteVm(1L, null);

        // When
        Set<ConstraintViolation<CartItemDeleteVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemDeleteVm_whenQuantityIsZero_shouldHaveValidationError() {
        // Given
        CartItemDeleteVm vm = new CartItemDeleteVm(1L, 0);

        // When
        Set<ConstraintViolation<CartItemDeleteVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemDeleteVm_whenQuantityIsNegative_shouldHaveValidationError() {
        // Given
        CartItemDeleteVm vm = new CartItemDeleteVm(1L, -1);

        // When
        Set<ConstraintViolation<CartItemDeleteVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemDeleteVm_whenAllFieldsValid_shouldHaveNoValidationErrors() {
        // Given
        CartItemDeleteVm vm = new CartItemDeleteVm(1L, 5);

        // When
        Set<ConstraintViolation<CartItemDeleteVm>> violations = validator.validate(vm);

        // Then
        assertTrue(violations.isEmpty());
    }
}
