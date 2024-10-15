package com.yas.order.mapper;

import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.viewmodel.order.OrderBriefVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderMapper {
    @Mapping(target = "phone", source = "billingAddressVm.phone")
    OrderItemCsv toCsv(OrderBriefVm orderItem);
}
