package com.yas.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

import com.yas.commonlibrary.model.AbstractAuditEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "brand")
@Getter
@Setter
public class Brand extends AbstractAuditEntity {
    @OneToMany(mappedBy = "brand")
    List<Product> products;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String slug;
    private boolean isPublished;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Brand)) {
            return false;
        }
        return id != null && id.equals(((Brand) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}