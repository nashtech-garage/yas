package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartGetDetailVmTest {

    @Test
    void id_shouldReturnValuePassedToConstructor() {
        CartGetDetailVm vm = new CartGetDetailVm(5L, "customer-123", List.of());
        assertThat(vm.id()).isEqualTo(5L);
    }

    @Test
    void customerId_shouldReturnValuePassedToConstructor() {
        CartGetDetailVm vm = new CartGetDetailVm(5L, "customer-123", List.of());
        assertThat(vm.customerId()).isEqualTo("customer-123");
    }

    @Test
    void cartDetails_shouldReturnListPassedToConstructor() {
        List<CartDetailVm> details = List.of(new CartDetailVm(1L, 10L, 2));
        CartGetDetailVm vm = new CartGetDetailVm(5L, "customer-123", details);
        assertThat(vm.cartDetails()).hasSize(1);
        assertThat(vm.cartDetails().get(0)).isEqualTo(new CartDetailVm(1L, 10L, 2));
    }

    @Test
    void cartDetails_shouldReturnEmptyList_whenEmptyListPassed() {
        CartGetDetailVm vm = new CartGetDetailVm(1L, "cust", List.of());
        assertThat(vm.cartDetails()).isEmpty();
    }

    @Test
    void customerId_shouldAllowNull() {
        CartGetDetailVm vm = new CartGetDetailVm(1L, null, List.of());
        assertThat(vm.customerId()).isNull();
    }

    @Test
    void id_shouldAllowNull() {
        CartGetDetailVm vm = new CartGetDetailVm(null, "cust", List.of());
        assertThat(vm.id()).isNull();
    }

    @Test
    void cartDetails_shouldAllowNull() {
        CartGetDetailVm vm = new CartGetDetailVm(1L, "cust", null);
        assertThat(vm.cartDetails()).isNull();
    }

    @Test
    void cartDetails_shouldAllowMultipleItems() {
        List<CartDetailVm> details = List.of(
                new CartDetailVm(1L, 10L, 2),
                new CartDetailVm(2L, 20L, 3),
                new CartDetailVm(3L, 30L, 1)
        );
        CartGetDetailVm vm = new CartGetDetailVm(1L, "cust", details);
        assertThat(vm.cartDetails()).hasSize(3);
    }

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        List<CartDetailVm> details = List.of(new CartDetailVm(1L, 10L, 2));
        assertThat(new CartGetDetailVm(5L, "c", details))
                .isEqualTo(new CartGetDetailVm(5L, "c", details));
    }

    @Test
    void equals_shouldReturnFalse_whenIdDiffers() {
        assertThat(new CartGetDetailVm(1L, "c", List.of()))
                .isNotEqualTo(new CartGetDetailVm(2L, "c", List.of()));
    }

    @Test
    void equals_shouldReturnFalse_whenCustomerIdDiffers() {
        assertThat(new CartGetDetailVm(1L, "c1", List.of()))
                .isNotEqualTo(new CartGetDetailVm(1L, "c2", List.of()));
    }

    @Test
    void equals_shouldReturnFalse_whenCartDetailsDiffer() {
        List<CartDetailVm> d1 = List.of(new CartDetailVm(1L, 10L, 1));
        List<CartDetailVm> d2 = List.of(new CartDetailVm(2L, 20L, 2));
        assertThat(new CartGetDetailVm(1L, "c", d1))
                .isNotEqualTo(new CartGetDetailVm(1L, "c", d2));
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(new CartGetDetailVm(1L, "c", List.of())).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnTrue_forSameInstance() {
        CartGetDetailVm vm = new CartGetDetailVm(1L, "c", List.of());
        assertThat(vm).isEqualTo(vm);
    }

    @Test
    void hashCode_shouldBeEqual_forSameValues() {
        assertThat(new CartGetDetailVm(5L, "c", List.of()).hashCode())
                .isEqualTo(new CartGetDetailVm(5L, "c", List.of()).hashCode());
    }

    @Test
    void hashCode_shouldDiffer_forDifferentValues() {
        assertThat(new CartGetDetailVm(1L, "c", List.of()).hashCode())
                .isNotEqualTo(new CartGetDetailVm(2L, "c", List.of()).hashCode());
    }

    @Test
    void toString_shouldContainRecordName() {
        assertThat(new CartGetDetailVm(1L, "cust", List.of()).toString())
                .contains("CartGetDetailVm");
    }

    @Test
    void toString_shouldContainCustomerId() {
        assertThat(new CartGetDetailVm(1L, "my-customer", List.of()).toString())
                .contains("my-customer");
    }
}