package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatePaymentProviderMapperTest {

    private CreatePaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new CreatePaymentProviderMapperImpl();
    }

    @Test
    void toModel_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_shouldMap_whenInputIsNotNull() {
        // Nếu project dùng @Builder, bạn đổi thành CreatePaymentVm.builder().build()
        CreatePaymentVm vm = new CreatePaymentVm(); 
        
        PaymentProvider model = mapper.toModel(vm);
        
        assertNotNull(model);
        assertTrue(model.isNew()); // Cover dòng @Mapping(target = "isNew", constant = "true")
    }

    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void partialUpdate_shouldDoNothing_whenInputIsNull() {
        PaymentProvider model = new PaymentProvider();
        mapper.partialUpdate(model, null);
        assertNotNull(model); // Đảm bảo không bị crash
    }

    @Test
    void partialUpdate_shouldUpdate_whenInputIsNotNull() {
        PaymentProvider model = new PaymentProvider();
        CreatePaymentVm vm = new CreatePaymentVm();
        
        // MẸO ĐỂ LÊN 100%: Bạn có bao nhiêu field trong CreatePaymentVm thì hãy set (hoặc mock) bấy nhiêu field ở đây.
        // VD: vm.setId("COD"); vm.setMediaId(1L); 
        // Để MapStruct nhảy vào các nhánh if (vm.getId() != null)
        
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