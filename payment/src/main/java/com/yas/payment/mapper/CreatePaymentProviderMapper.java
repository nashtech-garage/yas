package com.yas.payment.mapper;

import com.yas.commonlibrary.mapper.EntityCreateUpdateMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreatePaymentProviderMapper extends
    EntityCreateUpdateMapper<PaymentProvider, CreatePaymentVm, PaymentProviderVm> {

    @Mapping(target = "isNew", constant = "true")
    @Override
    PaymentProvider toModel(CreatePaymentVm vm);
}