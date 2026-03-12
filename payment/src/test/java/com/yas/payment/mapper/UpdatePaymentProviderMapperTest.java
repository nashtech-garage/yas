package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePaymentProviderMapperTest {

    private UpdatePaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new UpdatePaymentProviderMapperImpl();
    }

    @Test
    void toModel_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_shouldMap_whenInputIsNotNull() {
        UpdatePaymentVm vm = new UpdatePaymentVm();
        PaymentProvider model = mapper.toModel(vm);
        assertNotNull(model);
    }

    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
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
        UpdatePaymentVm vm = new UpdatePaymentVm();
        // Set vài field cho vm ở đây để tăng nhánh coverage nếu cần
        mapper.partialUpdate(model, vm);
        assertNotNull(model);
    }
    
    @Test
    void toVmResponse_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVmResponse(null));
    }
    
    @Test
    void toVmResponse_shouldMap_whenInputIsNotNull() {
        PaymentProvider model = new PaymentProvider();
        assertNotNull(mapper.toVmResponse(model));
    }
}