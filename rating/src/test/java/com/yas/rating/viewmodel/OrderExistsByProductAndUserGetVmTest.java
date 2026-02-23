package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderExistsByProductAndUserGetVmTest {

    @Test
    void testConstructor_whenTrue_shouldCreateVmWithTrue() {
        // When
        OrderExistsByProductAndUserGetVm vm = new OrderExistsByProductAndUserGetVm(true);

        // Then
        assertThat(vm).isNotNull();
        assertThat(vm.isPresent()).isTrue();
    }

    @Test
    void testConstructor_whenFalse_shouldCreateVmWithFalse() {
        // When
        OrderExistsByProductAndUserGetVm vm = new OrderExistsByProductAndUserGetVm(false);

        // Then
        assertThat(vm).isNotNull();
        assertThat(vm.isPresent()).isFalse();
    }

    @Test
    void testRecord_shouldWorkAsExpected() {
        // Given
        OrderExistsByProductAndUserGetVm vm1 = new OrderExistsByProductAndUserGetVm(true);
        OrderExistsByProductAndUserGetVm vm2 = new OrderExistsByProductAndUserGetVm(true);
        OrderExistsByProductAndUserGetVm vm3 = new OrderExistsByProductAndUserGetVm(false);

        // Then
        assertThat(vm1).isEqualTo(vm2);
        assertThat(vm1).isNotEqualTo(vm3);
    }
}
