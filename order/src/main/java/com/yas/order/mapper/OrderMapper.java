package com.yas.order.mapper;

import com.yas.order.model.enumeration.EOrderStatus;

import java.time.ZonedDateTime;

public interface OrderMapper {
    Long getId();
    EOrderStatus getOrderStatus();
    ZonedDateTime getCreatedOn();
}
