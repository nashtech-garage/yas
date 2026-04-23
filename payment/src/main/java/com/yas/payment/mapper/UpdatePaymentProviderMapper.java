package com.yas.payment.mapper;

import com.yas.commonlibrary.mapper.EntityCreateUpdateMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdatePaymentProviderMapper extends
    EntityCreateUpdateMapper<PaymentProvider, UpdatePaymentVm, PaymentProviderVm> {
}