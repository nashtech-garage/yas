package com.yas.order.model;


import com.yas.order.model.enumeration.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbstractAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phone;
    private String email;
    private Long addressId;
    private String note;
    private float tax;
    private float discount;
    private int numberItem;
    private String couponCode;
    private BigDecimal totalPrice;
    private BigDecimal deliveryFee;
    private EOrderStatus orderStatus;
    private EDeliveryMethod deliveryMethod;
    private EDeliveryStatus deliveryStatus;
    private EPaymentMethod paymentMethod;
    private EPaymentStatus paymentStatus;
    @OneToMany(mappedBy = "orderId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<OrderItem> orderItems;

}
