package com.yas.cart.viewmodel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartItemPostVmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCartItemPostVmBuilder_withValidData_shouldCreateSuccessfully() {
        // Given & When
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(5)
            .build();

        // Then
        assertNotNull(vm);
        assertEquals(1L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testCartItemPostVm_withAllArgsConstructor_shouldCreateSuccessfully() {
        // Given & When
        CartItemPostVm vm = new CartItemPostVm(2L, 10);

        // Then
        assertNotNull(vm);
        assertEquals(2L, vm.productId());
        assertEquals(10, vm.quantity());
    }

    @Test
    void testCartItemPostVm_whenProductIdIsNull_shouldHaveValidationError() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(null)
            .quantity(5)
            .build();

        // When
        Set<ConstraintViolation<CartItemPostVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
    }

    @Test
    void testCartItemPostVm_whenQuantityIsNull_shouldHaveValidationError() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(null)
            .build();

        // When
        Set<ConstraintViolation<CartItemPostVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPostVm_whenQuantityIsZero_shouldHaveValidationError() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(0)
            .build();

        // When
        Set<ConstraintViolation<CartItemPostVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPostVm_whenQuantityIsNegative_shouldHaveValidationError() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(-5)
            .build();

        // When
        Set<ConstraintViolation<CartItemPostVm>> violations = validator.validate(vm);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void testCartItemPostVm_whenAllFieldsValid_shouldHaveNoValidationErrors() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(5)
            .build();

        // When
        Set<ConstraintViolation<CartItemPostVm>> violations = validator.validate(vm);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCartItemPostVm_productId_shouldReturnCorrectValue() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(999L)
            .quantity(1)
            .build();

        // When & Then
        assertEquals(999L, vm.productId());
    }

    @Test
    void testCartItemPostVm_quantity_shouldReturnCorrectValue() {
        // Given
        CartItemPostVm vm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(100)
            .build();

        // When & Then
        assertEquals(100, vm.quantity());
    }
}
