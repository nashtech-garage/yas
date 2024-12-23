package com.yas.payment.mapper;

import com.yas.commonlibrary.mapper.BaseMapper;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentProviderMapper extends BaseMapper<PaymentProvider, PaymentProviderVm> {
}