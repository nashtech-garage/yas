package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentProviderMapperImplTest {

    private PaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentProviderMapperImpl();
    }

    @Test
    void toModel_withValidVm_shouldMapCorrectly() {
        PaymentProviderVm vm = new PaymentProviderVm("PAYPAL", "PayPal", "http://config", 1, 100L, "http://icon");

        PaymentProvider result = mapper.toModel(vm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("PAYPAL");
        assertThat(result.getName()).isEqualTo("PayPal");
        assertThat(result.getConfigureUrl()).isEqualTo("http://config");
        assertThat(result.getMediaId()).isEqualTo(100L);
        assertThat(result.getVersion()).isEqualTo(1);
    }

    @Test
    void toModel_withNull_shouldReturnNull() {
        assertThat(mapper.toModel(null)).isNull();
    }

    @Test
    void toVm_withValidModel_shouldMapCorrectly() {
        PaymentProvider model = PaymentProvider.builder()
                .id("COD")
                .name("Cash on Delivery")
                .configureUrl("http://config-cod")
                .version(2)
                .mediaId(200L)
                .build();

        PaymentProviderVm result = mapper.toVm(model);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("COD");
        assertThat(result.getName()).isEqualTo("Cash on Delivery");
        assertThat(result.getConfigureUrl()).isEqualTo("http://config-cod");
        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getMediaId()).isEqualTo(200L);
    }

    @Test
    void toVm_withNull_shouldReturnNull() {
        assertThat(mapper.toVm(null)).isNull();
    }

    @Test
    void partialUpdate_withValidVm_shouldUpdateModel() {
        PaymentProvider model = PaymentProvider.builder()
                .id("OLD_ID")
                .name("Old Name")
                .configureUrl("http://old")
                .version(1)
                .mediaId(10L)
                .build();

        PaymentProviderVm vm = new PaymentProviderVm("NEW_ID", "New Name", "http://new", 5, 20L, null);

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("NEW_ID");
        assertThat(model.getName()).isEqualTo("New Name");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new");
        assertThat(model.getMediaId()).isEqualTo(20L);
        assertThat(model.getVersion()).isEqualTo(5);
    }

    @Test
    void partialUpdate_withNull_shouldNotModifyModel() {
        PaymentProvider model = PaymentProvider.builder()
                .id("KEEP")
                .name("Keep Name")
                .build();

        mapper.partialUpdate(model, null);

        assertThat(model.getId()).isEqualTo("KEEP");
        assertThat(model.getName()).isEqualTo("Keep Name");
    }

    @Test
    void partialUpdate_withNullFields_shouldNotOverwrite() {
        PaymentProvider model = PaymentProvider.builder()
                .id("ORIGINAL")
                .name("Original Name")
                .configureUrl("http://original")
                .mediaId(50L)
                .build();

        PaymentProviderVm vm = new PaymentProviderVm(null, null, null, 0, null, null);

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("ORIGINAL");
        assertThat(model.getName()).isEqualTo("Original Name");
        assertThat(model.getConfigureUrl()).isEqualTo("http://original");
        assertThat(model.getMediaId()).isEqualTo(50L);
    }
}
