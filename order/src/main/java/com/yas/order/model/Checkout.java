package com.yas.order.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yas.commonlibrary.model.AbstractAuditEntity;
import com.yas.order.model.enumeration.CheckoutState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;

    private String note;

    @Column(name = "promotion_code")
    private String promotionCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CheckoutState checkoutState;

    @SuppressWarnings("unused")
    private String progress;

    @SuppressWarnings("unused")
    private String customerId;

    @SuppressWarnings("unused")
    private String shipmentMethodId;

    @SuppressWarnings("unused")
    private String shipmentServiceId;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @SuppressWarnings("unused")
    private Long shippingAddressId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private CheckoutAddress checkoutAddress;

    @SuppressWarnings("unused")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "last_error", columnDefinition = "jsonb")
    private String lastError;

    @SuppressWarnings("unused")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private String attributes;

    @SuppressWarnings("unused")
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @SuppressWarnings("unused")
    @Builder.Default
    private BigDecimal totalShipmentFee = BigDecimal.ZERO;

    @SuppressWarnings("unused")
    @Builder.Default
    private BigDecimal totalShipmentTax = BigDecimal.ZERO;

    @SuppressWarnings("unused")
    private BigDecimal totalTax;

    @SuppressWarnings("unused")
    @Builder.Default
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<CheckoutItem> checkoutItems = new ArrayList<>();
}