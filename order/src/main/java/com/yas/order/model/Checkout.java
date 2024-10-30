package com.yas.order.model;

import com.yas.order.model.enumeration.CheckoutState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "checkout")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout extends AbstractAuditEntity {

    @Id
    private String id;

    private String email;

    private String note;

    @Column(name = "promotion_code")
    private String couponCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CheckoutState checkoutState;

    @SuppressWarnings("unused")
    private String progress;

    @SuppressWarnings("unused")
    private Long customerId;

    @SuppressWarnings("unused")
    private String shipmentMethodId;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @SuppressWarnings("unused")
    private Long shippingAddressId;

    @SuppressWarnings("unused")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "last_error", columnDefinition = "jsonb")
    private String lastError;

    @SuppressWarnings("unused")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private String attributes;

    @SuppressWarnings("unused")
    private BigDecimal totalAmount;

    @SuppressWarnings("unused")
    private BigDecimal totalShipmentFee;

    @SuppressWarnings("unused")
    private BigDecimal totalShipmentTax;

    @SuppressWarnings("unused")
    private BigDecimal totalTax;

    @SuppressWarnings("unused")
    private BigDecimal totalDiscountAmount;

}