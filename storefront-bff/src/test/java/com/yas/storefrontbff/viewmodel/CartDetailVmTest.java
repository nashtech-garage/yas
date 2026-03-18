package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartDetailVmTest {

    @Test
    void id_shouldReturnValuePassedToConstructor() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 3);
        assertThat(vm.id()).isEqualTo(1L);
    }

    @Test
    void productId_shouldReturnValuePassedToConstructor() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 3);
        assertThat(vm.productId()).isEqualTo(42L);
    }

    @Test
    void quantity_shouldReturnValuePassedToConstructor() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 3);
        assertThat(vm.quantity()).isEqualTo(3);
    }

    @Test
    void quantity_shouldAllowZero() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 0);
        assertThat(vm.quantity()).isZero();
    }

    @Test
    void id_shouldAllowNull() {
        CartDetailVm vm = new CartDetailVm(null, 42L, 3);
        assertThat(vm.id()).isNull();
    }

    @Test
    void productId_shouldAllowNull() {
        CartDetailVm vm = new CartDetailVm(1L, null, 3);
        assertThat(vm.productId()).isNull();
    }

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isEqualTo(new CartDetailVm(1L, 42L, 3));
    }

    @Test
    void equals_shouldReturnFalse_whenIdDiffers() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isNotEqualTo(new CartDetailVm(2L, 42L, 3));
    }

    @Test
    void equals_shouldReturnFalse_whenProductIdDiffers() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isNotEqualTo(new CartDetailVm(1L, 99L, 3));
    }

    @Test
    void equals_shouldReturnFalse_whenQuantityDiffers() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isNotEqualTo(new CartDetailVm(1L, 42L, 5));
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToDifferentType() {
        assertThat(new CartDetailVm(1L, 42L, 3)).isNotEqualTo("other");
    }

    @Test
    void equals_shouldReturnTrue_forSameInstance() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 3);
        assertThat(vm).isEqualTo(vm);
    }

    @Test
    void hashCode_shouldBeEqual_forSameValues() {
        assertThat(new CartDetailVm(1L, 42L, 3).hashCode())
                .isEqualTo(new CartDetailVm(1L, 42L, 3).hashCode());
    }

    @Test
    void hashCode_shouldDiffer_forDifferentValues() {
        assertThat(new CartDetailVm(1L, 42L, 3).hashCode())
                .isNotEqualTo(new CartDetailVm(2L, 42L, 3).hashCode());
    }

    @Test
    void toString_shouldContainRecordName() {
        assertThat(new CartDetailVm(1L, 42L, 3).toString()).contains("CartDetailVm");
    }

    @Test
    void toString_shouldContainAllFieldValues() {
        CartDetailVm vm = new CartDetailVm(1L, 42L, 3);
        String str = vm.toString();
        assertThat(str).contains("1").contains("42").contains("3");
    }
}