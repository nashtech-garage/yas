package com.yas.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends AbstractAuditEntity {

    private String customerId;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<CartItem> cartItems = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return getId() != null && getId().equals(((Cart) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
