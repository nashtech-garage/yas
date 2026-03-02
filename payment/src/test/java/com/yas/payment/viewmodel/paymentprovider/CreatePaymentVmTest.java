package com.yas.payment.viewmodel.paymentprovider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePaymentVmTest {

    @Test
    void testCreatePaymentVmExtendsPaymentProviderReqVm() {
        // Given & When
        CreatePaymentVm vm = new CreatePaymentVm();

        // Then
        assertNotNull(vm);
        assertTrue(vm instanceof PaymentProviderReqVm);
    }

    @Test
    void testCreatePaymentVmInheritsFields() {
        // Given
        CreatePaymentVm vm = new CreatePaymentVm();

        // When
        vm.setId("new-provider");
        vm.setEnabled(true);
        vm.setName("New Provider");
        vm.setConfigureUrl("/new/config");
        vm.setLandingViewComponentName("NewComponent");
        vm.setAdditionalSettings("{\"test\":\"value\"}");
        vm.setMediaId(789L);

        // Then
        assertEquals("new-provider", vm.getId());
        assertTrue(vm.isEnabled());
        assertEquals("New Provider", vm.getName());
        assertEquals("/new/config", vm.getConfigureUrl());
        assertEquals("NewComponent", vm.getLandingViewComponentName());
        assertEquals("{\"test\":\"value\"}", vm.getAdditionalSettings());
        assertEquals(789L, vm.getMediaId());
    }

    @Test
    void testCreatePaymentVmDefaultState() {
        // Given & When
        CreatePaymentVm vm = new CreatePaymentVm();

        // Then
        assertNull(vm.getId());
        assertFalse(vm.isEnabled());
        assertNull(vm.getName());
        assertNull(vm.getConfigureUrl());
    }
}
