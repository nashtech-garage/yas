package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePaymentProviderMapperImplTest {

    private UpdatePaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new UpdatePaymentProviderMapperImpl();
    }

    @Test
    void toModel_withValidVm_shouldMapCorrectly() {
        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("PAYPAL");
        vm.setEnabled(true);
        vm.setName("PayPal");
        vm.setConfigureUrl("http://paypal-config");
        vm.setLandingViewComponentName("PaypalComponent");
        vm.setAdditionalSettings("{\"key\":\"val\"}");
        vm.setMediaId(100L);

        PaymentProvider result = mapper.toModel(vm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("PAYPAL");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getName()).isEqualTo("PayPal");
        assertThat(result.getConfigureUrl()).isEqualTo("http://paypal-config");
        assertThat(result.getLandingViewComponentName()).isEqualTo("PaypalComponent");
        assertThat(result.getAdditionalSettings()).isEqualTo("{\"key\":\"val\"}");
        assertThat(result.getMediaId()).isEqualTo(100L);
    }

    @Test
    void toModel_withNull_shouldReturnNull() {
        assertThat(mapper.toModel(null)).isNull();
    }

    @Test
    void toVm_withValidModel_shouldMapCorrectly() {
        PaymentProvider model = PaymentProvider.builder()
                .id("COD")
                .enabled(false)
                .name("Cash on Delivery")
                .configureUrl("http://cod")
                .landingViewComponentName("CodComponent")
                .additionalSettings("{\"cod\":true}")
                .mediaId(200L)
                .build();

        UpdatePaymentVm result = mapper.toVm(model);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("COD");
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getName()).isEqualTo("Cash on Delivery");
        assertThat(result.getConfigureUrl()).isEqualTo("http://cod");
        assertThat(result.getLandingViewComponentName()).isEqualTo("CodComponent");
        assertThat(result.getAdditionalSettings()).isEqualTo("{\"cod\":true}");
        assertThat(result.getMediaId()).isEqualTo(200L);
    }

    @Test
    void toVm_withNull_shouldReturnNull() {
        assertThat(mapper.toVm(null)).isNull();
    }

    @Test
    void toVmResponse_withValidModel_shouldMapToPaymentProviderVm() {
        PaymentProvider model = PaymentProvider.builder()
                .id("STRIPE")
                .name("Stripe")
                .configureUrl("http://stripe")
                .version(5)
                .mediaId(300L)
                .build();

        PaymentProviderVm result = mapper.toVmResponse(model);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("STRIPE");
        assertThat(result.getName()).isEqualTo("Stripe");
        assertThat(result.getConfigureUrl()).isEqualTo("http://stripe");
        assertThat(result.getVersion()).isEqualTo(5);
        assertThat(result.getMediaId()).isEqualTo(300L);
    }

    @Test
    void toVmResponse_withNull_shouldReturnNull() {
        assertThat(mapper.toVmResponse(null)).isNull();
    }

    @Test
    void partialUpdate_withValidVm_shouldUpdateModel() {
        PaymentProvider model = PaymentProvider.builder()
                .id("OLD")
                .enabled(false)
                .name("Old")
                .configureUrl("http://old")
                .landingViewComponentName("OldComp")
                .additionalSettings("{\"old\":1}")
                .mediaId(10L)
                .build();

        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("NEW");
        vm.setEnabled(true);
        vm.setName("New");
        vm.setConfigureUrl("http://new");
        vm.setLandingViewComponentName("NewComp");
        vm.setAdditionalSettings("{\"new\":1}");
        vm.setMediaId(20L);

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("NEW");
        assertThat(model.isEnabled()).isTrue();
        assertThat(model.getName()).isEqualTo("New");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new");
        assertThat(model.getLandingViewComponentName()).isEqualTo("NewComp");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"new\":1}");
        assertThat(model.getMediaId()).isEqualTo(20L);
    }

    @Test
    void partialUpdate_withNull_shouldNotModifyModel() {
        PaymentProvider model = PaymentProvider.builder()
                .id("KEEP")
                .name("Keep")
                .build();

        mapper.partialUpdate(model, null);

        assertThat(model.getId()).isEqualTo("KEEP");
        assertThat(model.getName()).isEqualTo("Keep");
    }

    @Test
    void partialUpdate_withNullFields_shouldNotOverwriteExisting() {
        PaymentProvider model = PaymentProvider.builder()
                .id("ORIG")
                .name("Original")
                .configureUrl("http://orig")
                .landingViewComponentName("OrigComp")
                .additionalSettings("{\"orig\":1}")
                .mediaId(50L)
                .build();

        UpdatePaymentVm vm = new UpdatePaymentVm();
        // all fields null except enabled (primitive, defaults false)

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("ORIG");
        assertThat(model.getName()).isEqualTo("Original");
        assertThat(model.getConfigureUrl()).isEqualTo("http://orig");
        assertThat(model.getLandingViewComponentName()).isEqualTo("OrigComp");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"orig\":1}");
        assertThat(model.getMediaId()).isEqualTo(50L);
    }
}
