package com.yas.payment.viewmodel.paymentprovider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePaymentVmTest {

    @Test
    void testUpdatePaymentVmExtendsPaymentProviderReqVm() {
        // Given & When
        UpdatePaymentVm vm = new UpdatePaymentVm();

        // Then
        assertNotNull(vm);
        assertTrue(vm instanceof PaymentProviderReqVm);
    }

    @Test
    void testUpdatePaymentVmInheritsFields() {
        // Given
        UpdatePaymentVm vm = new UpdatePaymentVm();

        // When
        vm.setId("update-provider");
        vm.setEnabled(false);
        vm.setName("Updated Provider");
        vm.setConfigureUrl("/update/config");
        vm.setLandingViewComponentName("UpdateComponent");
        vm.setAdditionalSettings("{\"updated\":\"true\"}");
        vm.setMediaId(999L);

        // Then
        assertEquals("update-provider", vm.getId());
        assertFalse(vm.isEnabled());
        assertEquals("Updated Provider", vm.getName());
        assertEquals("/update/config", vm.getConfigureUrl());
        assertEquals("UpdateComponent", vm.getLandingViewComponentName());
        assertEquals("{\"updated\":\"true\"}", vm.getAdditionalSettings());
        assertEquals(999L, vm.getMediaId());
    }

    @Test
    void testUpdatePaymentVmDefaultState() {
        // Given & When
        UpdatePaymentVm vm = new UpdatePaymentVm();

        // Then
        assertNull(vm.getId());
        assertFalse(vm.isEnabled());
        assertNull(vm.getName());
        assertNull(vm.getConfigureUrl());
    }
}
