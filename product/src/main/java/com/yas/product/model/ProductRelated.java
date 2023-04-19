package com.yas.product.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "product_related")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRelated {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "related_product_id")
    private Product relatedProduct;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductRelated)) {
            return false;
        }
        return id != null && id.equals(((ProductRelated) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
