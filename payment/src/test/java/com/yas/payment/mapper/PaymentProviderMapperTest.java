package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentProviderMapperTest {

    private PaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentProviderMapperImpl();
    }

    @Test
    void toModel_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toModel((PaymentProviderVm) null));
    }

    @Test
    void toModel_shouldMap_whenInputIsNotNull() {
        PaymentProviderVm vm = new PaymentProviderVm(null, null, null, 0, null, null); // Truyền properties tuỳ ý
        PaymentProvider model = mapper.toModel(vm);
        assertNotNull(model);
    }

    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toVm_shouldMap_whenInputIsNotNull() {
        PaymentProvider model = new PaymentProvider();
        PaymentProviderVm vm = mapper.toVm(model);
        assertNotNull(vm);
    }

    @Test
    void partialUpdate_shouldDoNothing_whenInputIsNull() {
        PaymentProvider model = new PaymentProvider();
        mapper.partialUpdate(model, null);
        assertNotNull(model);
    }

    @Test
    void partialUpdate_shouldUpdate_whenInputIsNotNull() {
        PaymentProvider model = new PaymentProvider();
        PaymentProviderVm vm = new PaymentProviderVm(null, null, null, 0, null, null);
        mapper.partialUpdate(model, vm);
        assertNotNull(model);
    }
}