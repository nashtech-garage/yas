package com.yas.order.model.csv;

import com.yas.commonlibrary.csv.BaseCsv;
import com.yas.commonlibrary.csv.anotation.CsvColumn;
import com.yas.commonlibrary.csv.anotation.CsvName;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.experimental.SuperBuilder;

@CsvName(fileName = "Orders")
@SuperBuilder
public class OrderItemCsv extends BaseCsv {

    @CsvColumn(columnName = "Order status")
    private OrderStatus orderStatus;

    @CsvColumn(columnName = "Payment status")
    private PaymentStatus paymentStatus;

    @CsvColumn(columnName = "Email")
    private String email;

    @CsvColumn(columnName = "Phone")
    private String phone;

    @CsvColumn(columnName = "Order total")
    private BigDecimal totalPrice;

    @CsvColumn(columnName = "Shipping status")
    private DeliveryStatus deliveryStatus;

    @CsvColumn(columnName = "Created on")
    private ZonedDateTime createdOn;
}