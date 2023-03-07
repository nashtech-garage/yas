package com.yas.product.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "brand")
@Getter
@Setter
public class Brand extends AbstractAuditEntity {

  private String name;

  private String slug;

  @OneToMany(mappedBy = "brand")
  List<Product> products;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Brand)) {
      return false;
    }
    return getId() != null && getId().equals(((Brand) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }
}
