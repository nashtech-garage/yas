package com.yas.payment.model;

import com.yas.payment.model.enumeration.EPaymentMethod;
import com.yas.payment.model.enumeration.EPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String checkoutId;
    private BigDecimal amount;
    private BigDecimal paymentFee;
    @Enumerated(EnumType.STRING)
    private EPaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private EPaymentStatus paymentStatus;
    public String gatewayTransactionId;
    private String failureMessage;
    @CreationTimestamp
    private ZonedDateTime createdOn;
    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;
}
