package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EOrderStatus;
import com.yas.order.model.enumeration.EPaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCsvExportVm(

        Long id,
        String email,
        Long shippingAddressId,
        String shippingAddressContactName,
        String shippingAddressPhone,
        String shippingAddressLine1,
        String shippingAddressLine2,
        String shippingCity,
        String shippingZipCode,
        Long shippingDistrictId,
        String shippingDistrictName,
        Long shippingStateOrProvinceId,
        String shippingStateOrProvinceName,
        Long shippingCountryId,
        String shippingCountryName,
        Long billingAddressId,
        String billingAddressContactName,
        String billingAddressPhone,
        String billingAddressLine1,
        String billingAddressLine2,
        String billingCity,
        String billingZipCode,
        Long billingDistrictId,
        String billingDistrictName,
        Long billingStateOrProvinceId,
        String billingStateOrProvinceName,
        Long billingCountryId,
        String billingCountryName,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        EOrderStatus orderStatus,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentStatus paymentStatus

) {
    public static OrderCsvExportVm fromModel(Order order) {
        OrderAddress shippingAddress = order.getShippingAddressId();
        OrderAddress billingAddress = order.getBillingAddressId();

        return OrderCsvExportVm.builder()
                .id(order.getId())
                .email(order.getEmail())
                .shippingAddressId(shippingAddress.getId())
                .shippingAddressContactName(shippingAddress.getContactName())
                .shippingAddressPhone(shippingAddress.getPhone())
                .shippingAddressLine1(shippingAddress.getAddressLine1())
                .shippingAddressLine2(shippingAddress.getAddressLine2())
                .shippingZipCode(shippingAddress.getZipCode())
                .shippingDistrictId(shippingAddress.getDistrictId())
                .shippingDistrictName(shippingAddress.getDistrictName())
                .shippingCity(shippingAddress.getCity())
                .shippingStateOrProvinceId(shippingAddress.getStateOrProvinceId())
                .shippingStateOrProvinceName(shippingAddress.getStateOrProvinceName())
                .shippingCountryId(shippingAddress.getCountryId())
                .shippingCountryName(shippingAddress.getCountryName())
                .billingAddressId(billingAddress.getId())
                .billingAddressContactName(billingAddress.getContactName())
                .billingAddressPhone(billingAddress.getPhone())
                .billingAddressLine1(billingAddress.getAddressLine1())
                .billingAddressLine2(billingAddress.getAddressLine2())
                .billingZipCode(billingAddress.getZipCode())
                .billingDistrictId(billingAddress.getDistrictId())
                .billingDistrictName(billingAddress.getDistrictName())
                .billingCity(billingAddress.getCity())
                .billingStateOrProvinceId(billingAddress.getStateOrProvinceId())
                .billingStateOrProvinceName(billingAddress.getStateOrProvinceName())
                .billingCountryId(billingAddress.getCountryId())
                .billingCountryName(billingAddress.getCountryName())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .note(order.getNote())
                .numberItem(order.getNumberItem())
                .deliveryFee(order.getDeliveryFee())
                .couponCode(order.getCouponCode())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}