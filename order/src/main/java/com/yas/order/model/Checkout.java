package com.yas.order.model;

import com.yas.order.model.enumeration.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "checkout")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout extends AbstractAuditEntity{
    @Id
    private String id;
    private String email;
    private String note;
    private String couponCode;
    @Enumerated(EnumType.STRING)
    private ECheckoutState checkoutState;
    private
    @OneToMany(mappedBy = "checkoutId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<CheckoutItem> checkoutItem;

}