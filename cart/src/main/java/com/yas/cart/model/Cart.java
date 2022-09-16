package com.yas.cart.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "cart")
public class Cart extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerID;

    @OneToMany(mappedBy = "cart")
    private List<CartDetail> cartDetails = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
