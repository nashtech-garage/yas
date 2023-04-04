package com.yas.order.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal productPrice;
    private String note;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal taxPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "id")
    private Order orderId;
}
