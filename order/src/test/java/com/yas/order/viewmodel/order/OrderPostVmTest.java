package com.yas.order.viewmodel.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderPostVmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testOrderPostVm_withValidData_shouldHaveNoViolations() {
        OrderPostVm vm = buildValidOrderPostVm();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testOrderPostVm_whenCheckoutIdIsBlank_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("")
                .email("test@test.com")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(new BigDecimal("100.00"))
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("checkoutId")));
    }

    @Test
    void testOrderPostVm_whenEmailIsBlank_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(new BigDecimal("100.00"))
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testOrderPostVm_whenTotalPriceIsNull_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@test.com")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(null)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("totalPrice")));
    }

    @Test
    void testOrderPostVm_whenDeliveryMethodIsNull_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@test.com")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(new BigDecimal("100.00"))
                .deliveryMethod(null)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("deliveryMethod")));
    }

    @Test
    void testOrderPostVm_whenShippingAddressIsNull_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@test.com")
                .shippingAddressPostVm(null)
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(new BigDecimal("100.00"))
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("shippingAddressPostVm")));
    }

    @Test
    void testOrderPostVm_whenOrderItemPostVmsIsNull_shouldHaveViolation() {
        OrderPostVm vm = OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@test.com")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .totalPrice(new BigDecimal("100.00"))
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(null)
                .build();

        Set<ConstraintViolation<OrderPostVm>> violations = validator.validate(vm);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("orderItemPostVms")));
    }

    @Test
    void testOrderPostVm_recordAccessors_shouldReturnCorrectValues() {
        OrderPostVm vm = buildValidOrderPostVm();

        assertEquals("checkout-1", vm.checkoutId());
        assertEquals("test@test.com", vm.email());
        assertNotNull(vm.shippingAddressPostVm());
        assertNotNull(vm.billingAddressPostVm());
        assertEquals("Note", vm.note());
        assertEquals(5.0f, vm.tax());
        assertEquals(10.0f, vm.discount());
        assertEquals(2, vm.numberItem());
        assertEquals(new BigDecimal("100.00"), vm.totalPrice());
        assertEquals("COUPON", vm.couponCode());
    }

    private OrderPostVm buildValidOrderPostVm() {
        return OrderPostVm.builder()
                .checkoutId("checkout-1")
                .email("test@test.com")
                .shippingAddressPostVm(buildAddressPostVm())
                .billingAddressPostVm(buildAddressPostVm())
                .note("Note")
                .tax(5.0f)
                .discount(10.0f)
                .numberItem(2)
                .totalPrice(new BigDecimal("100.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("COUPON")
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of())
                .build();
    }

    private OrderAddressPostVm buildAddressPostVm() {
        return OrderAddressPostVm.builder()
                .contactName("John Doe")
                .phone("+123456789")
                .addressLine1("123 Main St")
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
