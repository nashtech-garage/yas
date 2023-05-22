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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String name;
    public boolean isEnabled;
    public String configureUrl;
    public String landingViewComponentName;

    /// Additional setting for specific provider. Stored as json string.
    public String additionalSettings;
}
