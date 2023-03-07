package com.yas.product.model.attribute;

import com.yas.product.model.AbstractAuditEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_attribute")
@Getter
@Setter
public class ProductAttribute extends AbstractAuditEntity {
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_attribute_group_id")
    private ProductAttributeGroup productAttributeGroup;

    @OneToMany(mappedBy = "productAttribute")
    private List<ProductAttributeValue> attributeValues = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAttribute)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductAttribute) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
