package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemVmTest {

    @Test
    void productId_shouldReturnValuePassedToConstructor() {
        CartItemVm vm = new CartItemVm(99L, 5);
        assertThat(vm.productId()).isEqualTo(99L);
    }

    @Test
    void quantity_shouldReturnValuePassedToConstructor() {
        CartItemVm vm = new CartItemVm(99L, 5);
        assertThat(vm.quantity()).isEqualTo(5);
    }

    @Test
    void productId_shouldAllowNull() {
        CartItemVm vm = new CartItemVm(null, 5);
        assertThat(vm.productId()).isNull();
    }

    @Test
    void quantity_shouldAllowZero() {
        CartItemVm vm = new CartItemVm(1L, 0);
        assertThat(vm.quantity()).isZero();
    }

    @Test
    void quantity_shouldAllowNegative() {
        CartItemVm vm = new CartItemVm(1L, -1);
        assertThat(vm.quantity()).isEqualTo(-1);
    }

    // -----------------------------------------------------------------------
    // fromCartDetailVm factory method
    // -----------------------------------------------------------------------

    @Test
    void fromCartDetailVm_shouldMapProductId() {
        CartDetailVm detail = new CartDetailVm(10L, 55L, 7);
        assertThat(CartItemVm.fromCartDetailVm(detail).productId()).isEqualTo(55L);
    }

    @Test
    void fromCartDetailVm_shouldMapQuantity() {
        CartDetailVm detail = new CartDetailVm(10L, 55L, 7);
        assertThat(CartItemVm.fromCartDetailVm(detail).quantity()).isEqualTo(7);
    }

    @Test
    void fromCartDetailVm_shouldNotExposeCartDetailId() {
        // The cart detail's own id (999L) must not appear as productId on CartItemVm
        CartDetailVm detail = new CartDetailVm(999L, 1L, 2);
        CartItemVm item = CartItemVm.fromCartDetailVm(detail);
        assertThat(item.productId()).isEqualTo(1L).isNotEqualTo(999L);
    }

    @Test
    void fromCartDetailVm_shouldPreserveZeroQuantity() {
        CartDetailVm detail = new CartDetailVm(1L, 10L, 0);
        assertThat(CartItemVm.fromCartDetailVm(detail).quantity()).isZero();
    }

    @Test
    void fromCartDetailVm_shouldPreserveLargeQuantity() {
        CartDetailVm detail = new CartDetailVm(1L, 10L, Integer.MAX_VALUE);
        assertThat(CartItemVm.fromCartDetailVm(detail).quantity()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void fromCartDetailVm_resultShouldEqualDirectConstruction() {
        CartDetailVm detail = new CartDetailVm(7L, 33L, 4);
        CartItemVm fromFactory = CartItemVm.fromCartDetailVm(detail);
        CartItemVm direct = new CartItemVm(33L, 4);
        assertThat(fromFactory).isEqualTo(direct);
    }

    // -----------------------------------------------------------------------
    // equals / hashCode / toString
    // -----------------------------------------------------------------------

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        assertThat(new CartItemVm(1L, 3)).isEqualTo(new CartItemVm(1L, 3));
    }

    @Test
    void equals_shouldReturnFalse_whenProductIdDiffers() {
        assertThat(new CartItemVm(1L, 3)).isNotEqualTo(new CartItemVm(2L, 3));
    }

    @Test
    void equals_shouldReturnFalse_whenQuantityDiffers() {
        assertThat(new CartItemVm(1L, 3)).isNotEqualTo(new CartItemVm(1L, 5));
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        assertThat(new CartItemVm(1L, 3)).isNotEqualTo(null);
    }

    @Test
    void equals_shouldReturnTrue_forSameInstance() {
        CartItemVm vm = new CartItemVm(1L, 3);
        assertThat(vm).isEqualTo(vm);
    }

    @Test
    void hashCode_shouldBeEqual_forSameValues() {
        assertThat(new CartItemVm(1L, 3).hashCode())
                .isEqualTo(new CartItemVm(1L, 3).hashCode());
    }

    @Test
    void hashCode_shouldDiffer_forDifferentValues() {
        assertThat(new CartItemVm(1L, 3).hashCode())
                .isNotEqualTo(new CartItemVm(2L, 3).hashCode());
    }

    @Test
    void toString_shouldContainRecordName() {
        assertThat(new CartItemVm(1L, 3).toString()).contains("CartItemVm");
    }

    @Test
    void toString_shouldContainProductIdAndQuantity() {
        String str = new CartItemVm(99L, 5).toString();
        assertThat(str).contains("99").contains("5");
    }
}