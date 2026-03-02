package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatePaymentProviderMapperImplTest {

    private CreatePaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreatePaymentProviderMapperImpl();
    }

    @Test
    void toVm_withValidModel_shouldMapCorrectly() {
        PaymentProvider model = PaymentProvider.builder()
                .id("PAYPAL")
                .enabled(true)
                .name("PayPal")
                .configureUrl("http://config")
                .landingViewComponentName("PaypalComponent")
                .additionalSettings("{\"key\":\"value\"}")
                .mediaId(100L)
                .build();

        CreatePaymentVm result = mapper.toVm(model);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("PAYPAL");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getName()).isEqualTo("PayPal");
        assertThat(result.getConfigureUrl()).isEqualTo("http://config");
        assertThat(result.getLandingViewComponentName()).isEqualTo("PaypalComponent");
        assertThat(result.getAdditionalSettings()).isEqualTo("{\"key\":\"value\"}");
        assertThat(result.getMediaId()).isEqualTo(100L);
    }

    @Test
    void toVm_withNull_shouldReturnNull() {
        assertThat(mapper.toVm(null)).isNull();
    }

    @Test
    void toModel_withValidVm_shouldMapCorrectly() {
        CreatePaymentVm vm = new CreatePaymentVm();
        vm.setId("COD");
        vm.setEnabled(false);
        vm.setName("Cash on Delivery");
        vm.setConfigureUrl("http://cod-config");
        vm.setLandingViewComponentName("CodComponent");
        vm.setAdditionalSettings("{\"cod\":true}");
        vm.setMediaId(200L);

        PaymentProvider result = mapper.toModel(vm);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("COD");
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getName()).isEqualTo("Cash on Delivery");
        assertThat(result.getConfigureUrl()).isEqualTo("http://cod-config");
        assertThat(result.getLandingViewComponentName()).isEqualTo("CodComponent");
        assertThat(result.getAdditionalSettings()).isEqualTo("{\"cod\":true}");
        assertThat(result.getMediaId()).isEqualTo(200L);
        assertThat(result.isNew()).isTrue();
    }

    @Test
    void toModel_withNull_shouldReturnNull() {
        assertThat(mapper.toModel(null)).isNull();
    }

    @Test
    void toVmResponse_withValidModel_shouldMapToPaymentProviderVm() {
        PaymentProvider model = PaymentProvider.builder()
                .id("STRIPE")
                .name("Stripe")
                .configureUrl("http://stripe")
                .version(3)
                .mediaId(300L)
                .build();

        PaymentProviderVm result = mapper.toVmResponse(model);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("STRIPE");
        assertThat(result.getName()).isEqualTo("Stripe");
        assertThat(result.getConfigureUrl()).isEqualTo("http://stripe");
        assertThat(result.getVersion()).isEqualTo(3);
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
                .name("Old Name")
                .configureUrl("http://old")
                .landingViewComponentName("OldComponent")
                .additionalSettings("{\"old\":true}")
                .mediaId(10L)
                .build();

        CreatePaymentVm vm = new CreatePaymentVm();
        vm.setId("NEW");
        vm.setEnabled(true);
        vm.setName("New Name");
        vm.setConfigureUrl("http://new");
        vm.setLandingViewComponentName("NewComponent");
        vm.setAdditionalSettings("{\"new\":true}");
        vm.setMediaId(20L);

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("NEW");
        assertThat(model.isEnabled()).isTrue();
        assertThat(model.getName()).isEqualTo("New Name");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new");
        assertThat(model.getLandingViewComponentName()).isEqualTo("NewComponent");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"new\":true}");
        assertThat(model.getMediaId()).isEqualTo(20L);
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
    void partialUpdate_withNullFields_shouldNotOverwriteExistingValues() {
        PaymentProvider model = PaymentProvider.builder()
                .id("ORIGINAL")
                .name("Original Name")
                .configureUrl("http://original")
                .landingViewComponentName("OrigComponent")
                .additionalSettings("{\"orig\":true}")
                .mediaId(50L)
                .build();

        CreatePaymentVm vm = new CreatePaymentVm();
        // All fields are null except enabled (primitive defaults to false)

        mapper.partialUpdate(model, vm);

        assertThat(model.getId()).isEqualTo("ORIGINAL");
        assertThat(model.getName()).isEqualTo("Original Name");
        assertThat(model.getConfigureUrl()).isEqualTo("http://original");
        assertThat(model.getLandingViewComponentName()).isEqualTo("OrigComponent");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"orig\":true}");
        assertThat(model.getMediaId()).isEqualTo(50L);
    }
}
