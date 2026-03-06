package com.yas.cart.viewmodel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemPutVmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCartItemPutVm_withValidQuantity_shouldCreateSuccessfully() {
        // Given & When
        CartItemPutVm vm = new CartItemPutVm(5);

        // Then
        assertNotNull(vm);
        assertEquals(5, vm.quantity());
    }

    @Test
    void testCartItemPutVm_whenQuantityIsNull_shouldHaveValidationError() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(null);

        // When
        Set<ConstraintViolation<CartItemPutVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPutVm_whenQuantityIsZero_shouldHaveValidationError() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(0);

        // When
        Set<ConstraintViolation<CartItemPutVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPutVm_whenQuantityIsNegative_shouldHaveValidationError() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(-1);

        // When
        Set<ConstraintViolation<CartItemPutVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPutVm_whenQuantityIsOne_shouldHaveNoValidationErrors() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(1);

        // When
        Set<ConstraintViolation<CartItemPutVm>> violations = validator.validate(vm);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCartItemPutVm_whenQuantityIsLarge_shouldHaveNoValidationErrors() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(1000);

        // When
        Set<ConstraintViolation<CartItemPutVm>> violations = validator.validate(vm);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCartItemPutVm_quantity_shouldReturnCorrectValue() {
        // Given
        CartItemPutVm vm = new CartItemPutVm(42);

        // When & Then
        assertEquals(42, vm.quantity());
    }
}
