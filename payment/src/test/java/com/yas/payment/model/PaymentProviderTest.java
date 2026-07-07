package com.yas.payment.model;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PaymentProviderTest {

    private PaymentProvider paymentProvider;

    @BeforeEach
    void setUp() {
        paymentProvider = PaymentProvider.builder()
                .id("provider_123")
                .enabled(true)
                .name("PayPal")
                .configureUrl("https://paypal.com/configure")
                .landingViewComponentName("PaypalLandingComponent")
                .additionalSettings("{\"mode\":\"sandbox\"}")
                .mediaId(1L)
                .build();
        
        // Set version and audit fields for testing
        ReflectionTestUtils.setField(paymentProvider, "version", 0);
        ReflectionTestUtils.setField(paymentProvider, "createdBy", "test_user");
        ReflectionTestUtils.setField(paymentProvider, "createdOn", ZonedDateTime.now());
        ReflectionTestUtils.setField(paymentProvider, "lastModifiedBy", "test_user");
        ReflectionTestUtils.setField(paymentProvider, "lastModifiedOn", ZonedDateTime.now());
    }

    @Test
    @DisplayName("Should create payment provider using builder")
    void testBuilder_ShouldCreatePaymentProvider() {
        assertThat(paymentProvider).isNotNull();
        assertThat(paymentProvider.getId()).isEqualTo("provider_123");
        assertThat(paymentProvider.isEnabled()).isTrue();
        assertThat(paymentProvider.getName()).isEqualTo("PayPal");
        assertThat(paymentProvider.getConfigureUrl()).isEqualTo("https://paypal.com/configure");
        assertThat(paymentProvider.getLandingViewComponentName()).isEqualTo("PaypalLandingComponent");
        assertThat(paymentProvider.getAdditionalSettings()).isEqualTo("{\"mode\":\"sandbox\"}");
        assertThat(paymentProvider.getMediaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should create payment provider with no args constructor")
    void testNoArgsConstructor_ShouldCreateEmptyPaymentProvider() {
        PaymentProvider provider = new PaymentProvider();
        
        assertThat(provider).isNotNull();
        assertThat(provider.getId()).isNull();
        assertThat(provider.isEnabled()).isFalse();
        assertThat(provider.getName()).isNull();
        assertThat(provider.getConfigureUrl()).isNull();
        assertThat(provider.getLandingViewComponentName()).isNull();
        assertThat(provider.getAdditionalSettings()).isNull();
        assertThat(provider.getMediaId()).isNull();
        assertThat(provider.getVersion()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should create payment provider with all args constructor")
    void testAllArgsConstructor_ShouldCreatePaymentProvider() {
        PaymentProvider provider = new PaymentProvider(
            "provider_456",
            false,
            "Stripe",
            "https://stripe.com/configure",
            "StripeLandingComponent",
            "{\"api_version\":\"2023-10-16\"}",
            2L,
            1,
            false
        );
        
        assertThat(provider.getId()).isEqualTo("provider_456");
        assertThat(provider.isEnabled()).isFalse();
        assertThat(provider.getName()).isEqualTo("Stripe");
        assertThat(provider.getConfigureUrl()).isEqualTo("https://stripe.com/configure");
        assertThat(provider.getLandingViewComponentName()).isEqualTo("StripeLandingComponent");
        assertThat(provider.getAdditionalSettings()).isEqualTo("{\"api_version\":\"2023-10-16\"}");
        assertThat(provider.getMediaId()).isEqualTo(2L);
        assertThat(provider.getVersion()).isEqualTo(1);
        assertThat(provider.isNew()).isFalse();
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void testSetAndGetId() {
        paymentProvider.setId("new_provider_id");
        assertThat(paymentProvider.getId()).isEqualTo("new_provider_id");
    }

    @Test
    @DisplayName("Should set and get enabled status correctly")
    void testSetAndGetEnabled() {
        paymentProvider.setEnabled(false);
        assertThat(paymentProvider.isEnabled()).isFalse();
        
        paymentProvider.setEnabled(true);
        assertThat(paymentProvider.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void testSetAndGetName() {
        paymentProvider.setName("Momo");
        assertThat(paymentProvider.getName()).isEqualTo("Momo");
    }

    @Test
    @DisplayName("Should set and get configure URL correctly")
    void testSetAndGetConfigureUrl() {
        paymentProvider.setConfigureUrl("https://momo.vn/configure");
        assertThat(paymentProvider.getConfigureUrl()).isEqualTo("https://momo.vn/configure");
    }

    @Test
    @DisplayName("Should set and get landing view component name correctly")
    void testSetAndGetLandingViewComponentName() {
        paymentProvider.setLandingViewComponentName("MomoLandingComponent");
        assertThat(paymentProvider.getLandingViewComponentName()).isEqualTo("MomoLandingComponent");
    }

    @Test
    @DisplayName("Should set and get additional settings correctly")
    void testSetAndGetAdditionalSettings() {
        String settings = "{\"timeout\":30,\"retry\":3}";
        paymentProvider.setAdditionalSettings(settings);
        assertThat(paymentProvider.getAdditionalSettings()).isEqualTo(settings);
    }

    @Test
    @DisplayName("Should set and get media id correctly")
    void testSetAndGetMediaId() {
        paymentProvider.setMediaId(100L);
        assertThat(paymentProvider.getMediaId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should set and get version correctly")
    void testSetAndGetVersion() {
        paymentProvider.setVersion(5);
        assertThat(paymentProvider.getVersion()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get isNew flag correctly")
    void testSetAndGetIsNew() {
        paymentProvider.setNew(true);
        assertThat(paymentProvider.isNew()).isTrue();
        
        paymentProvider.setNew(false);
        assertThat(paymentProvider.isNew()).isFalse();
    }


    @Test
    @DisplayName("Should handle null values correctly")
    void testHandleNullValues() {
        PaymentProvider provider = PaymentProvider.builder()
                .id(null)
                .name(null)
                .configureUrl(null)
                .landingViewComponentName(null)
                .additionalSettings(null)
                .mediaId(null)
                .build();
        
        assertThat(provider.getId()).isNull();
        assertThat(provider.getName()).isNull();
        assertThat(provider.getConfigureUrl()).isNull();
        assertThat(provider.getLandingViewComponentName()).isNull();
        assertThat(provider.getAdditionalSettings()).isNull();
        assertThat(provider.getMediaId()).isNull();
    }

    @Test
    @DisplayName("Should handle different enabled states")
    void testDifferentEnabledStates() {
        PaymentProvider enabledProvider = PaymentProvider.builder()
                .id("provider_1")
                .enabled(true)
                .build();
        
        PaymentProvider disabledProvider = PaymentProvider.builder()
                .id("provider_2")
                .enabled(false)
                .build();
        
        assertThat(enabledProvider.isEnabled()).isTrue();
        assertThat(disabledProvider.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        PaymentProvider provider = PaymentProvider.builder()
                .id("")
                .name("")
                .configureUrl("")
                .landingViewComponentName("")
                .additionalSettings("")
                .build();
        
        assertThat(provider.getId()).isEmpty();
        assertThat(provider.getName()).isEmpty();
        assertThat(provider.getConfigureUrl()).isEmpty();
        assertThat(provider.getLandingViewComponentName()).isEmpty();
        assertThat(provider.getAdditionalSettings()).isEmpty();
    }

    @Test
    @DisplayName("Should inherit audit fields from AbstractAuditEntity")
    void testInheritAuditFields() {
        ZonedDateTime now = ZonedDateTime.now();
        
        paymentProvider.setCreatedBy("admin");
        paymentProvider.setCreatedOn(now);
        paymentProvider.setLastModifiedBy("admin");
        paymentProvider.setLastModifiedOn(now);
        
        assertThat(paymentProvider.getCreatedBy()).isEqualTo("admin");
        assertThat(paymentProvider.getCreatedOn()).isEqualTo(now);
        assertThat(paymentProvider.getLastModifiedBy()).isEqualTo("admin");
        assertThat(paymentProvider.getLastModifiedOn()).isEqualTo(now);
    }

    @SuppressWarnings("java:S1192")  
    @Test
    @DisplayName("Should handle JSON string in additional settings")
    void testAdditionalSettingsAsJson() {
        String jsonSettings = "{\n" +
                "  \"api_key\": \"test_key\",\n" +
                "  \"webhook_url\": \"https://example.com/webhook\",\n" +
                "  \"timeout\": 30\n" +
                "}";
        
        paymentProvider.setAdditionalSettings(jsonSettings);
        assertThat(paymentProvider.getAdditionalSettings()).isEqualTo(jsonSettings);
        assertThat(paymentProvider.getAdditionalSettings()).contains("api_key");
        assertThat(paymentProvider.getAdditionalSettings()).contains("webhook_url");
    }
}