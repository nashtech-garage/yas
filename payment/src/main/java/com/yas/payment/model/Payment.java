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
    private BigDecimal amount;
    private BigDecimal paymentFee;
    private EPaymentMethod paymentMethod;
    private EPaymentStatus paymentStatus;
    private String failureMessage;
    @CreationTimestamp
    private ZonedDateTime createdOn;
    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;
}
