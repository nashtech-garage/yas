package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.model.enumeration.EPaymentStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderExportingDetailVm(
        Long id,
        String email,
        BigDecimal totalPrice,
        String shippingAddressContactName,
        String shippingAddressPhone,
        String shippingAddressLine1,
        String shippingAddressLine2,
        String shippingAddressCity,
        String shippingAddressZipCode,
        String shippingAddressDistrictName,
        String shippingAddressStateOrProvinceName,
        String shippingAddressCountryName,
        String billingAddressContactName,
        String billingAddressPhone,
        String billingAddressLine1,
        String billingAddressLine2,
        String billingAddressCity,
        String billingAddressZipCode,
        String billingAddressDistrictName,
        String billingAddressStateOrProvinceName,
        String billingAddressCountryName,
        String note,
        float tax,
        float discount,
        int numberItem,
        EOrderStatus orderStatus,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentStatus paymentStatus,
        ZonedDateTime createdOn,
        BigDecimal deliveryFee,
        String couponCode

) {
    public static OrderExportingDetailVm fromModel(Order order){
        return new OrderExportingDetailVm(
                order.getId(),
                order.getEmail(),
                order.getTotalPrice(),
                order.getShippingAddressId().getContactName(),
                order.getShippingAddressId().getPhone(),
                order.getShippingAddressId().getAddressLine1(),
                order.getShippingAddressId().getAddressLine2(),
                order.getShippingAddressId().getCity(),
                order.getShippingAddressId().getZipCode(),
                order.getShippingAddressId().getDistrictName(),
                order.getShippingAddressId().getStateOrProvinceName(),
                order.getShippingAddressId().getCountryName(),

                order.getBillingAddressId().getContactName(),
                order.getBillingAddressId().getPhone(),
                order.getBillingAddressId().getAddressLine1(),
                order.getBillingAddressId().getAddressLine2(),
                order.getBillingAddressId().getCity(),
                order.getBillingAddressId().getZipCode(),
                order.getBillingAddressId().getDistrictName(),
                order.getBillingAddressId().getStateOrProvinceName(),
                order.getBillingAddressId().getCountryName(),
                order.getNote(),
                order.getTax(),
                order.getDiscount(),
                order.getNumberItem(),
                order.getOrderStatus(),
                order.getDeliveryMethod(),
                order.getDeliveryStatus(),
                order.getPaymentStatus(),
                order.getCreatedOn(),
                order.getDeliveryFee(),
                order.getCouponCode());
    }
}
