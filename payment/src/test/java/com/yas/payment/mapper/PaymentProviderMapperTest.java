package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PaymentProviderMapperTest {

    private final PaymentProviderMapper mapper = Mappers.getMapper(PaymentProviderMapper.class);

    @Test
    void toVm_shouldMapCorrectly() {
        PaymentProvider model = new PaymentProvider();
        model.setId("paypal");
        model.setName("PayPal");

        PaymentProviderVm vm = mapper.toVm(model);

        assertEquals("paypal", vm.getId());
        assertEquals("PayPal", vm.getName());
    }

    @Test
    void toVm_shouldReturnNull_whenModelIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toModel_shouldMapCorrectly() {
        PaymentProviderVm vm = new PaymentProviderVm("paypal", "PayPal", "configure", 1, 1L, "icon");

        PaymentProvider model = mapper.toModel(vm);

        assertEquals("paypal", model.getId());
        assertEquals("PayPal", model.getName());
    }

    @Test
    void toModel_shouldReturnNull_whenVmIsNull() {
        assertNull(mapper.toModel(null));
    }
}
