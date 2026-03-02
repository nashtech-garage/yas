package com.yas.payment.viewmodel.paymentprovider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProviderReqVmTest {

    @Test
    void testPaymentProviderReqVmGettersAndSetters() {
        // Given
        PaymentProviderReqVm vm = new PaymentProviderReqVm();

        // When
        vm.setId("provider-1");
        vm.setEnabled(true);
        vm.setName("Provider One");
        vm.setConfigureUrl("/provider/config");
        vm.setLandingViewComponentName("ProviderComponent");
        vm.setAdditionalSettings("{\"key\":\"value\"}");
        vm.setMediaId(123L);

        // Then
        assertEquals("provider-1", vm.getId());
        assertTrue(vm.isEnabled());
        assertEquals("Provider One", vm.getName());
        assertEquals("/provider/config", vm.getConfigureUrl());
        assertEquals("ProviderComponent", vm.getLandingViewComponentName());
        assertEquals("{\"key\":\"value\"}", vm.getAdditionalSettings());
        assertEquals(123L, vm.getMediaId());
    }

    @Test
    void testPaymentProviderReqVmDefaultValues() {
        // Given & When
        PaymentProviderReqVm vm = new PaymentProviderReqVm();

        // Then
        assertNotNull(vm);
        assertNull(vm.getId());
        assertFalse(vm.isEnabled());
        assertNull(vm.getName());
        assertNull(vm.getConfigureUrl());
        assertNull(vm.getLandingViewComponentName());
        assertNull(vm.getAdditionalSettings());
        assertNull(vm.getMediaId());
    }

    @Test
    void testPaymentProviderReqVmWithAllFields() {
        // Given
        PaymentProviderReqVm vm = new PaymentProviderReqVm();
        
        // When
        vm.setId("cod");
        vm.setEnabled(false);
        vm.setName("Cash on Delivery");
        vm.setConfigureUrl("/cod/config");
        vm.setLandingViewComponentName("CODComponent");
        vm.setAdditionalSettings("{}");
        vm.setMediaId(456L);

        // Then
        assertEquals("cod", vm.getId());
        assertFalse(vm.isEnabled());
        assertEquals("Cash on Delivery", vm.getName());
        assertEquals("/cod/config", vm.getConfigureUrl());
        assertEquals("CODComponent", vm.getLandingViewComponentName());
        assertEquals("{}", vm.getAdditionalSettings());
        assertEquals(456L, vm.getMediaId());
    }
}
