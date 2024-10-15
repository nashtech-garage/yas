package com.yas.order.model.csv;

import com.yas.commonlibrary.csv.BaseCsv;
import com.yas.commonlibrary.csv.anotation.CSVColumn;
import com.yas.commonlibrary.csv.anotation.CSVName;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@CSVName(fileName = "Orders")
@Builder
public class OrderItemCsv extends BaseCsv {

    @CSVColumn(columnName = "Order id")
    private Long id;

    @CSVColumn(columnName = "Order status")
    private OrderStatus orderStatus;

    @CSVColumn(columnName = "Payment status")
    private PaymentStatus paymentStatus;

    @CSVColumn(columnName = "Email")
    private String email;

    @CSVColumn(columnName = "Phone")
    private String phone;

    @CSVColumn(columnName = "Order total")
    private BigDecimal totalPrice;

    @CSVColumn(columnName = "Shipping status")
    private DeliveryStatus deliveryStatus;

    @CSVColumn(columnName = "Created on")
    private ZonedDateTime createdOn;
}