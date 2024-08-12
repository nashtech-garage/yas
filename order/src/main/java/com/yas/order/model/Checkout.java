package com.yas.order.model;

import com.yas.order.model.enumeration.CheckoutState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String couponCode;
    @Enumerated(EnumType.STRING)
    private CheckoutState checkoutState;

    @OneToMany(mappedBy = "checkoutId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<CheckoutItem> checkoutItem;

}