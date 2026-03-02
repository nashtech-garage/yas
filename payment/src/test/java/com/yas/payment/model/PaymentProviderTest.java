package com.yas.payment.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProviderTest {

    @Test
    void testPaymentProviderBuilder() {
        // Given & When
        PaymentProvider provider = PaymentProvider.builder()
                .id("paypal")
                .enabled(true)
                .name("PayPal")
                .configureUrl("/paypal/config")
                .landingViewComponentName("PayPalComponent")
                .additionalSettings("{\"key\":\"value\"}")
                .mediaId(1L)
                .version(1)
                .isNew(true)
                .build();

        // Then
        assertNotNull(provider);
        assertEquals("paypal", provider.getId());
        assertTrue(provider.isEnabled());
        assertEquals("PayPal", provider.getName());
        assertEquals("/paypal/config", provider.getConfigureUrl());
        assertEquals("PayPalComponent", provider.getLandingViewComponentName());
        assertEquals("{\"key\":\"value\"}", provider.getAdditionalSettings());
        assertEquals(1L, provider.getMediaId());
        assertEquals(1, provider.getVersion());
        assertTrue(provider.isNew());
    }

    @Test
    void testPaymentProviderGettersAndSetters() {
        // Given
        PaymentProvider provider = new PaymentProvider();

        // When
        provider.setId("stripe");
        provider.setEnabled(false);
        provider.setName("Stripe");
        provider.setConfigureUrl("/stripe/config");
        provider.setLandingViewComponentName("StripeComponent");
        provider.setAdditionalSettings("{\"apiKey\":\"test\"}");
        provider.setMediaId(2L);
        provider.setVersion(2);
        provider.setNew(false);

        // Then
        assertEquals("stripe", provider.getId());
        assertFalse(provider.isEnabled());
        assertEquals("Stripe", provider.getName());
        assertEquals("/stripe/config", provider.getConfigureUrl());
        assertEquals("StripeComponent", provider.getLandingViewComponentName());
        assertEquals("{\"apiKey\":\"test\"}", provider.getAdditionalSettings());
        assertEquals(2L, provider.getMediaId());
        assertEquals(2, provider.getVersion());
        assertFalse(provider.isNew());
    }

    @Test
    void testPaymentProviderNoArgsConstructor() {
        // When
        PaymentProvider provider = new PaymentProvider();

        // Then
        assertNotNull(provider);
        assertNull(provider.getId());
        assertNull(provider.getName());
        assertFalse(provider.isEnabled());
    }

    @Test
    void testPaymentProviderAllArgsConstructor() {
        // When
        PaymentProvider provider = new PaymentProvider(
                "cod",
                true,
                "Cash on Delivery",
                "/cod/config",
                "CODComponent",
                "{}",
                3L,
                1,
                true
        );

        // Then
        assertNotNull(provider);
        assertEquals("cod", provider.getId());
        assertTrue(provider.isEnabled());
        assertEquals("Cash on Delivery", provider.getName());
        assertEquals("/cod/config", provider.getConfigureUrl());
        assertEquals("CODComponent", provider.getLandingViewComponentName());
        assertEquals("{}", provider.getAdditionalSettings());
        assertEquals(3L, provider.getMediaId());
        assertEquals(1, provider.getVersion());
        assertTrue(provider.isNew());
    }

    @Test
    void testIsNewMethod() {
        // Given
        PaymentProvider provider = new PaymentProvider();
        
        // When
        provider.setNew(true);
        
        // Then
        assertTrue(provider.isNew());
        
        // When
        provider.setNew(false);
        
        // Then
        assertFalse(provider.isNew());
    }

    @Test
    void testPersistableInterface() {
        // Given
        PaymentProvider provider = PaymentProvider.builder()
                .id("test-provider")
                .isNew(true)
                .build();

        // Then - verify it implements Persistable correctly
        assertEquals("test-provider", provider.getId());
        assertTrue(provider.isNew());
    }
}
