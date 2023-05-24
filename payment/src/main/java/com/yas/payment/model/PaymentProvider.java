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
@Table(name = "payment_provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean isEnabled;
    private String name;
    private String configureUrl;
    private String landingViewComponentName;
    private String additionalSettings;

}
