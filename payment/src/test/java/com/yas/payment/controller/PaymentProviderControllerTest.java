package com.yas.payment.controller;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    private CreatePaymentVm createPaymentVm;
    private UpdatePaymentVm updatePaymentVm;
    private PaymentProviderVm paymentProviderVm;

    @BeforeEach
    void setUp() {
        createPaymentVm = new CreatePaymentVm();
        createPaymentVm.setId("paypal");
        createPaymentVm.setName("PayPal");
        createPaymentVm.setConfigureUrl("/paypal/config");
        createPaymentVm.setEnabled(true);

        updatePaymentVm = new UpdatePaymentVm();
        updatePaymentVm.setId("paypal");
        updatePaymentVm.setName("PayPal Updated");
        updatePaymentVm.setConfigureUrl("/paypal/new-config");
        updatePaymentVm.setEnabled(false);

        paymentProviderVm = new PaymentProviderVm(
                "paypal",
                "PayPal",
                "/paypal/config",
                1,
                100L,
                "https://example.com/icon.png"
        );
    }

    @Test
    void testCreate_success() {
        // Given
        when(paymentProviderService.create(any(CreatePaymentVm.class)))
                .thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> result = paymentProviderController.create(createPaymentVm);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("paypal", result.getBody().getId());
        assertEquals("PayPal", result.getBody().getName());
        verify(paymentProviderService, times(1)).create(any(CreatePaymentVm.class));
    }

    @Test
    void testUpdate_success() {
        // Given
        PaymentProviderVm updatedVm = new PaymentProviderVm(
                "paypal",
                "PayPal Updated",
                "/paypal/new-config",
                2,
                100L,
                "https://example.com/icon.png"
        );

        when(paymentProviderService.update(any(UpdatePaymentVm.class)))
                .thenReturn(updatedVm);

        // When
        ResponseEntity<PaymentProviderVm> result = paymentProviderController.update(updatePaymentVm);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("PayPal Updated", result.getBody().getName());
        assertEquals("/paypal/new-config", result.getBody().getConfigureUrl());
        verify(paymentProviderService, times(1)).update(any(UpdatePaymentVm.class));
    }

    @Test
    void testGetAll_success() {
        // Given
        PaymentProviderVm provider1 = new PaymentProviderVm(
                "paypal", "PayPal", "/paypal/config", 1, 100L, "icon1.png"
        );
        PaymentProviderVm provider2 = new PaymentProviderVm(
                "stripe", "Stripe", "/stripe/config", 1, 200L, "icon2.png"
        );
        List<PaymentProviderVm> providers = Arrays.asList(provider1, provider2);

        Pageable pageable = PageRequest.of(0, 10);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
                .thenReturn(providers);

        // When
        ResponseEntity<List<PaymentProviderVm>> result = paymentProviderController.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals("paypal", result.getBody().get(0).getId());
        assertEquals("stripe", result.getBody().get(1).getId());
        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(any(Pageable.class));
    }

    @Test
    void testGetAll_emptyList() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
                .thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<PaymentProviderVm>> result = paymentProviderController.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(any(Pageable.class));
    }

    @Test
    void testCreate_withNullResponse() {
        // Given
        when(paymentProviderService.create(any(CreatePaymentVm.class)))
                .thenReturn(null);

        // When
        ResponseEntity<PaymentProviderVm> result = paymentProviderController.create(createPaymentVm);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(result.getBody());
        verify(paymentProviderService, times(1)).create(any(CreatePaymentVm.class));
    }
}
