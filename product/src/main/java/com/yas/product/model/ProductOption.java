package com.yas.product.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "product_option")
@Getter
@Setter
public class ProductOption extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOption)) {
            return false;
        }
        return id != null && id.equals(((ProductOption) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
