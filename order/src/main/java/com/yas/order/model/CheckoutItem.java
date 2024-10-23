package com.yas.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkout_item")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItem extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @Column(name = "checkout_id")
    private String checkoutId;

    @Column(name = "name")
    private String productName;

    private String description;

    private int quantity;

    @Column(name = "price")
    private BigDecimal productPrice;

    private Long tax;

    @SuppressWarnings("unused")
    private BigDecimal shipmentFee;

    @SuppressWarnings("unused")
    private BigDecimal shipmentTax;

    private BigDecimal discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", insertable = false, updatable = false)
    private Checkout checkout;
}
