package com.yas.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PaymentProviderControllerTest {

    private PaymentProviderService paymentProviderService;
    private PaymentProviderController paymentProviderController;

    @BeforeEach
    void setUp() {
        paymentProviderService = mock(PaymentProviderService.class);
        paymentProviderController = new PaymentProviderController(paymentProviderService);
    }

    @Test
    @DisplayName("create - should return 201 CREATED with created payment provider")
    void create_ShouldReturn201_WithCreatedPaymentProvider() {
        // Given
        CreatePaymentVm createPaymentVm = new CreatePaymentVm();
        createPaymentVm.setId("paypal");
        createPaymentVm.setName("PayPal");
        createPaymentVm.setConfigureUrl("https://configure.paypal.com");
        createPaymentVm.setEnabled(true);

        PaymentProviderVm expectedVm = new PaymentProviderVm("paypal", "PayPal", "https://configure.paypal.com", 0, null, null);

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(expectedVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("paypal", response.getBody().getId());
        assertEquals("PayPal", response.getBody().getName());
        verify(paymentProviderService, times(1)).create(createPaymentVm);
    }

    @Test
    @DisplayName("update - should return 200 OK with updated payment provider")
    void update_ShouldReturn200_WithUpdatedPaymentProvider() {
        // Given
        UpdatePaymentVm updatePaymentVm = new UpdatePaymentVm();
        updatePaymentVm.setId("paypal");
        updatePaymentVm.setName("PayPal Updated");
        updatePaymentVm.setConfigureUrl("https://new-configure.paypal.com");
        updatePaymentVm.setEnabled(true);

        PaymentProviderVm expectedVm = new PaymentProviderVm("paypal", "PayPal Updated", "https://new-configure.paypal.com", 1, null, null);

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(expectedVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PayPal Updated", response.getBody().getName());
        verify(paymentProviderService, times(1)).update(updatePaymentVm);
    }

    @Test
    @DisplayName("getAll - should return 200 OK with list of enabled payment providers")
    void getAll_ShouldReturn200_WithListOfEnabledPaymentProviders() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        PaymentProviderVm vm1 = new PaymentProviderVm("paypal", "PayPal", "https://configure.paypal.com", 0, null, null);
        PaymentProviderVm vm2 = new PaymentProviderVm("cod", "COD", "https://configure.cod.com", 0, null, null);
        List<PaymentProviderVm> expectedList = List.of(vm1, vm2);

        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(expectedList);

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("paypal", response.getBody().get(0).getId());
        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(pageable);
    }

    @Test
    @DisplayName("getAll - should return 200 OK with empty list when no providers enabled")
    void getAll_ShouldReturn200_WithEmptyList_WhenNoEnabledProviders() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(List.of());

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(pageable);
    }
}
