package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TaxRateControllerTest {

    @Mock
    TaxRateService taxRateService;

    @InjectMocks
    TaxRateController taxRateController;

    @Test
    void getPageableTaxRatesShouldReturnServicePage() {
        TaxRateListGetVm page = new TaxRateListGetVm(
            List.of(new TaxRateGetDetailVm(1L, 7.5, "70000", "Standard", "Ho Chi Minh", "Vietnam")),
            0,
            10,
            1,
            1,
            true
        );
        when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(page);

        ResponseEntity<TaxRateListGetVm> response = taxRateController.getPageableTaxRates(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(page);
    }

    @Test
    void getTaxRateShouldReturnServiceResult() {
        TaxRateVm taxRate = new TaxRateVm(1L, 7.5, "70000", 10L, 20L, 30L);
        when(taxRateService.findById(1L)).thenReturn(taxRate);

        ResponseEntity<TaxRateVm> response = taxRateController.getTaxRate(1L);

        assertThat(response.getBody()).isSameAs(taxRate);
    }

    @Test
    void createTaxRateShouldReturnCreatedLocation() {
        TaxRatePostVm request = new TaxRatePostVm(7.5, "70000", 10L, 20L, 30L);
        TaxRate created = TaxRate.builder()
            .id(1L)
            .rate(7.5)
            .zipCode("70000")
            .taxClass(TaxClass.builder().id(10L).name("Standard").build())
            .stateOrProvinceId(20L)
            .countryId(30L)
            .build();
        when(taxRateService.createTaxRate(request)).thenReturn(created);

        ResponseEntity<TaxRateVm> response = taxRateController.createTaxRate(
            request,
            UriComponentsBuilder.fromUri(URI.create("http://localhost"))
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasPath("/tax-rates/1");
        assertThat(response.getBody()).isEqualTo(TaxRateVm.fromModel(created));
    }

    @Test
    void updateTaxRateShouldReturnNoContent() {
        TaxRatePostVm request = new TaxRatePostVm(7.5, "70000", 10L, 20L, 30L);

        ResponseEntity<Void> response = taxRateController.updateTaxRate(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).updateTaxRate(request, 1L);
    }

    @Test
    void deleteTaxRateShouldReturnNoContent() {
        ResponseEntity<Void> response = taxRateController.deleteTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).delete(1L);
    }

    @Test
    void getTaxPercentByAddressShouldReturnServiceResult() {
        when(taxRateService.getTaxPercent(10L, 30L, 20L, "70000")).thenReturn(7.5);

        ResponseEntity<Double> response = taxRateController.getTaxPercentByAddress(10L, 30L, 20L, "70000");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(7.5);
    }

    @Test
    void getBatchTaxPercentsByAddressShouldReturnServiceResult() {
        List<TaxRateVm> taxRates = List.of(new TaxRateVm(1L, 7.5, "70000", 10L, 20L, 30L));
        when(taxRateService.getBulkTaxRate(List.of(10L), 30L, 20L, "70000")).thenReturn(taxRates);

        ResponseEntity<List<TaxRateVm>> response =
            taxRateController.getBatchTaxPercentsByAddress(List.of(10L), 30L, 20L, "70000");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(taxRates);
    }
}
