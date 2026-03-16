package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.service.PaymentProviderService;
import org.junit.jupiter.api.Test;

class AbstractPaymentHandlerTest {

    @Test
    void getPaymentSettings_ReturnsSettingsFromPaymentProviderService() {
        PaymentProviderService paymentProviderService = mock(PaymentProviderService.class);
        TestPaymentHandler handler = new TestPaymentHandler(paymentProviderService);

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL"))
                .thenReturn("apiKey=abc123");

        String settings = handler.fetchSettings("PAYPAL");

        assertEquals("apiKey=abc123", settings);
        verify(paymentProviderService).getAdditionalSettingsByPaymentProviderId("PAYPAL");
    }

    private static class TestPaymentHandler extends AbstractPaymentHandler {
        TestPaymentHandler(PaymentProviderService paymentProviderService) {
            super(paymentProviderService);
        }

        String fetchSettings(String providerId) {
            return getPaymentSettings(providerId);
        }
    }
}
