package com.yas.payment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProvider {
    @Id
    private String id;
    private boolean isEnabled;
    private String name;
    private String configureUrl;
    private String landingViewComponentName;
    private String additionalSettings;

}
