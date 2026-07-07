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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    private CreatePaymentVm createPaymentVm;
    private UpdatePaymentVm updatePaymentVm;
    private PaymentProviderVm paymentProviderVm;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Initialize test data for Create (extends PaymentProviderReqVm)
        createPaymentVm = new CreatePaymentVm();
        createPaymentVm.setName("PayPal");
        createPaymentVm.setConfigureUrl("https://paypal.com/configure");
        createPaymentVm.setMediaId(100L);
        // Note: setIconUrl() doesn't exist in CreatePaymentVm

        // Initialize test data for Update (extends PaymentProviderReqVm)
        updatePaymentVm = new UpdatePaymentVm();
        updatePaymentVm.setName("PayPal Updated");
        updatePaymentVm.setConfigureUrl("https://paypal.com/configure/new");
        updatePaymentVm.setMediaId(200L);
        // Note: setIconUrl() doesn't exist in UpdatePaymentVm

        // Initialize test data for Response
        paymentProviderVm = new PaymentProviderVm(
                "provider-123",
                "PayPal",
                "https://paypal.com/configure",
                1,
                100L,
                "https://example.com/paypal-icon.png"
        );

        // Initialize Pageable
        pageable = PageRequest.of(0, 10);
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreate_ShouldReturnCreatedStatus() {
        // Given
        when(paymentProviderService.create(any(CreatePaymentVm.class)))
                .thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreate_ShouldReturnCorrectBody() {
        // Given
        when(paymentProviderService.create(createPaymentVm)).thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(paymentProviderVm);
    }

    @Test
    void testCreate_ShouldReturnPaymentProviderWithCorrectData() {
        // Given
        when(paymentProviderService.create(createPaymentVm)).thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);
        PaymentProviderVm result = response.getBody();

        // Then
        assertThat(result.getId()).isEqualTo("provider-123");
        assertThat(result.getName()).isEqualTo("PayPal");
        assertThat(result.getConfigureUrl()).isEqualTo("https://paypal.com/configure");
        assertThat(result.getVersion()).isEqualTo(1);
        assertThat(result.getMediaId()).isEqualTo(100L);
        assertThat(result.getIconUrl()).isEqualTo("https://example.com/paypal-icon.png");
    }

    @Test
    void testCreate_WithNullConfigureUrl() {
        // Given
        CreatePaymentVm createWithNullUrl = new CreatePaymentVm();
        createWithNullUrl.setName("COD");
        createWithNullUrl.setConfigureUrl(null);
        createWithNullUrl.setMediaId(101L);

        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-456",
                "COD",
                null,
                1,
                101L,
                null
        );

        when(paymentProviderService.create(createWithNullUrl)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createWithNullUrl);

        // Then
        assertThat(response.getBody().getConfigureUrl()).isNull();
        assertThat(response.getBody().getIconUrl()).isNull();
    }

    @Test
    void testCreate_WithEmptyConfigureUrl() {
        // Given
        CreatePaymentVm createWithEmptyUrl = new CreatePaymentVm();
        createWithEmptyUrl.setName("Bank Transfer");
        createWithEmptyUrl.setConfigureUrl("");
        createWithEmptyUrl.setMediaId(102L);

        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-789",
                "Bank Transfer",
                "",
                1,
                102L,
                "https://example.com/bank-icon.png"
        );

        when(paymentProviderService.create(createWithEmptyUrl)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createWithEmptyUrl);

        // Then
        assertThat(response.getBody().getConfigureUrl()).isEmpty();
    }

    @Test
    void testCreate_WithNullMediaId() {
        // Given
        CreatePaymentVm createWithNullMedia = new CreatePaymentVm();
        createWithNullMedia.setName("Stripe");
        createWithNullMedia.setConfigureUrl("https://stripe.com/configure");
        createWithNullMedia.setMediaId(null);

        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-999",
                "Stripe",
                "https://stripe.com/configure",
                1,
                null,
                "https://example.com/stripe-icon.png"
        );

        when(paymentProviderService.create(createWithNullMedia)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createWithNullMedia);

        // Then
        assertThat(response.getBody().getMediaId()).isNull();
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdate_ShouldReturnOkStatus() {
        // Given
        when(paymentProviderService.update(any(UpdatePaymentVm.class)))
                .thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testUpdate_ShouldReturnCorrectBody() {
        // Given
        when(paymentProviderService.update(updatePaymentVm)).thenReturn(paymentProviderVm);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(paymentProviderVm);
    }

    @Test
    void testUpdate_ShouldUpdateAllFields() {
        // Given
        PaymentProviderVm updatedResponse = new PaymentProviderVm(
                "provider-123",
                "PayPal Updated",
                "https://paypal.com/configure/new",
                2,
                200L,
                "https://example.com/paypal-icon-updated.png"
        );

        when(paymentProviderService.update(updatePaymentVm)).thenReturn(updatedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);
        PaymentProviderVm result = response.getBody();

        // Then
        assertThat(result.getName()).isEqualTo("PayPal Updated");
        assertThat(result.getConfigureUrl()).isEqualTo("https://paypal.com/configure/new");
        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getMediaId()).isEqualTo(200L);
        assertThat(result.getIconUrl()).isEqualTo("https://example.com/paypal-icon-updated.png");
    }

    @Test
    void testUpdate_WithPartialUpdate() {
        // Given
        UpdatePaymentVm partialUpdate = new UpdatePaymentVm();
        partialUpdate.setName("Updated Name Only");

        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-123",
                "Updated Name Only",
                "https://paypal.com/configure",
                2,
                100L,
                "https://example.com/paypal-icon.png"
        );

        when(paymentProviderService.update(partialUpdate)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(partialUpdate);

        // Then
        assertThat(response.getBody().getName()).isEqualTo("Updated Name Only");
        assertThat(response.getBody().getConfigureUrl()).isEqualTo("https://paypal.com/configure");
    }

    @Test
    void testUpdate_WithNullMediaId() {
        // Given
        UpdatePaymentVm updateWithNullMedia = new UpdatePaymentVm();
        updateWithNullMedia.setName("Stripe");
        updateWithNullMedia.setConfigureUrl("https://stripe.com/configure");
        updateWithNullMedia.setMediaId(null);

        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-999",
                "Stripe",
                "https://stripe.com/configure",
                2,
                null,
                "https://example.com/stripe-icon.png"
        );

        when(paymentProviderService.update(updateWithNullMedia)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updateWithNullMedia);

        // Then
        assertThat(response.getBody().getMediaId()).isNull();
    }

    // ==================== GET ALL (ENABLED) TESTS ====================

    @Test
    void testGetAll_ShouldReturnOkStatus() {
        // Given
        List<PaymentProviderVm> paymentProviders = Arrays.asList(paymentProviderVm);
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(paymentProviders);

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetAll_ShouldReturnListOfPaymentProviders() {
        // Given
        PaymentProviderVm paymentProvider2 = new PaymentProviderVm(
                "provider-456",
                "Stripe",
                "https://stripe.com/configure",
                1,
                101L,
                "https://example.com/stripe-icon.png"
        );

        List<PaymentProviderVm> paymentProviders = Arrays.asList(paymentProviderVm, paymentProvider2);
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(paymentProviders);

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);
        List<PaymentProviderVm> result = response.getBody();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("provider-123");
        assertThat(result.get(0).getName()).isEqualTo("PayPal");
        assertThat(result.get(1).getId()).isEqualTo("provider-456");
        assertThat(result.get(1).getName()).isEqualTo("Stripe");
    }

    @Test
    void testGetAll_ShouldReturnEmptyListWhenNoProviders() {
        // Given
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        // Then
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testGetAll_WithPagination() {
        // Given
        Pageable customPageable = PageRequest.of(0, 5);
        List<PaymentProviderVm> paginatedProviders = Arrays.asList(paymentProviderVm);
        
        when(paymentProviderService.getEnabledPaymentProviders(customPageable))
                .thenReturn(paginatedProviders);

        // When
        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(customPageable);

        // Then
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void testGetAll_WithMultiplePages() {
        // Given
        Pageable firstPage = PageRequest.of(0, 1);
        Pageable secondPage = PageRequest.of(1, 1);
        
        PaymentProviderVm firstProvider = new PaymentProviderVm(
                "provider-1", "PayPal", "url1", 1, 100L, "icon1.png"
        );
        PaymentProviderVm secondProvider = new PaymentProviderVm(
                "provider-2", "Stripe", "url2", 1, 101L, "icon2.png"
        );

        when(paymentProviderService.getEnabledPaymentProviders(firstPage))
                .thenReturn(Arrays.asList(firstProvider));
        when(paymentProviderService.getEnabledPaymentProviders(secondPage))
                .thenReturn(Arrays.asList(secondProvider));

        // When
        ResponseEntity<List<PaymentProviderVm>> firstResponse = paymentProviderController.getAll(firstPage);
        ResponseEntity<List<PaymentProviderVm>> secondResponse = paymentProviderController.getAll(secondPage);

        // Then
        assertThat(firstResponse.getBody().get(0).getId()).isEqualTo("provider-1");
        assertThat(secondResponse.getBody().get(0).getId()).isEqualTo("provider-2");
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    void testCreate_WithLargeVersionNumber() {
        // Given
        PaymentProviderVm expectedResponse = new PaymentProviderVm(
                "provider-large",
                "PayPal",
                "https://paypal.com/configure",
                999,
                100L,
                "icon.png"
        );

        when(paymentProviderService.create(any(CreatePaymentVm.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);

        // Then
        assertThat(response.getBody().getVersion()).isEqualTo(999);
    }

    @Test
    void testUpdate_VersionShouldIncrease() {
        // Given
        PaymentProviderVm updatedResponse = new PaymentProviderVm(
                "provider-123",
                "PayPal Updated",
                "https://paypal.com/configure/new",
                2,
                100L,
                "icon.png"
        );

        when(paymentProviderService.update(updatePaymentVm)).thenReturn(updatedResponse);

        // When
        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);

        // Then
        assertThat(response.getBody().getVersion()).isEqualTo(2);
    }
}