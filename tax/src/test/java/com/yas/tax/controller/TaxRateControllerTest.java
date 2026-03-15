package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    private TaxRate taxRate;
    private TaxRatePostVm taxRatePostVm;

    @BeforeEach
    void setUp() {
        taxRate = TaxRate.builder().id(1L).rate(10.0).build();
        taxRatePostVm = mock(TaxRatePostVm.class);
    }

    @Test
    void getPageableTaxRates_ShouldReturnOk() {
        TaxRateListGetVm mockList = mock(TaxRateListGetVm.class);
        when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(mockList);

        ResponseEntity<TaxRateListGetVm> response = taxRateController.getPageableTaxRates(0, 10);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getTaxRate_ShouldReturnOk() {
        when(taxRateService.findById(1L)).thenReturn(mock(TaxRateVm.class));
        ResponseEntity<TaxRateVm> response = taxRateController.getTaxRate(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // @Test
    // void createTaxRate_ShouldReturnCreated() {
    //     when(taxRateService.createTaxRate(taxRatePostVm)).thenReturn(taxRate);
    //     UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

    //     ResponseEntity<TaxRateVm> response = taxRateController.createTaxRate(taxRatePostVm, uriBuilder);

    //     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    //     assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/tax-rates/1");
    // }

    @Test
    void updateTaxRate_ShouldReturnNoContent() {
        doNothing().when(taxRateService).updateTaxRate(any(), eq(1L));
        ResponseEntity<Void> response = taxRateController.updateTaxRate(1L, taxRatePostVm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteTaxRate_ShouldReturnNoContent() {
        doNothing().when(taxRateService).delete(1L);
        ResponseEntity<Void> response = taxRateController.deleteTaxRate(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getTaxPercentByAddress_ShouldReturnOk() {
        when(taxRateService.getTaxPercent(1L, 1L, 1L, "70000")).thenReturn(10.5);
        ResponseEntity<Double> response = taxRateController.getTaxPercentByAddress(1L, 1L, 1L, "70000");
        assertThat(response.getBody()).isEqualTo(10.5);
    }

    @Test
    void getBatchTaxPercentsByAddress_ShouldReturnOk() {
        when(taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "70000")).thenReturn(List.of(mock(TaxRateVm.class)));
        ResponseEntity<List<TaxRateVm>> response = taxRateController.getBatchTaxPercentsByAddress(List.of(1L), 1L, 1L, "70000");
        assertThat(response.getBody()).hasSize(1);
    }
}