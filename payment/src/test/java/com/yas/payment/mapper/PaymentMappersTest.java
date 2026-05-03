package com.yas.payment.mapper;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PaymentMappersTest {

    @Test
    void testPaymentProviderMapper() {
        // Khởi tạo trực tiếp class Impl do MapStruct sinh ra
        PaymentProviderMapper mapper = new PaymentProviderMapperImpl();
        
        // 1. Phủ xanh các nhánh kiểm tra null (chỉ test object đơn)
        assertNull(mapper.toVm((PaymentProvider) null));
        assertNull(mapper.toModel((PaymentProviderVm) null));

        // 2. Phủ xanh logic map dữ liệu thực tế (chỉ test object đơn)
        PaymentProvider model = new PaymentProvider();
        assertNotNull(mapper.toVm(model));

        PaymentProviderVm vm = mock(PaymentProviderVm.class);
        assertNotNull(mapper.toModel(vm));
    }

    @Test
    void testCreatePaymentProviderMapper() {
        CreatePaymentProviderMapper mapper = new CreatePaymentProviderMapperImpl();
        
        // Nhánh null
        assertNull(mapper.toVm((PaymentProvider) null));
        assertNull(mapper.toModel((CreatePaymentVm) null));
        
        // Nhánh có dữ liệu
        PaymentProvider model = new PaymentProvider();
        assertNotNull(mapper.toVm(model));
        
        CreatePaymentVm vm = mock(CreatePaymentVm.class);
        PaymentProvider mappedModel = mapper.toModel(vm);
        assertNotNull(mappedModel);
    }

    @Test
    void testUpdatePaymentProviderMapper() {
        UpdatePaymentProviderMapper mapper = new UpdatePaymentProviderMapperImpl();
        
        // Nhánh null
        assertNull(mapper.toVm((PaymentProvider) null));
        assertNull(mapper.toModel((UpdatePaymentVm) null));
        
        // Nhánh có dữ liệu
        PaymentProvider model = new PaymentProvider();
        assertNotNull(mapper.toVm(model));
        
        UpdatePaymentVm vm = mock(UpdatePaymentVm.class);
        assertNotNull(mapper.toModel(vm));
    }
}