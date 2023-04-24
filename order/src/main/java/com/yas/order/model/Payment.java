
package com.yas.order.model;


import com.yas.order.model.enumeration.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

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
    @OneToOne(mappedBy = "payment")
    private Order order;
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