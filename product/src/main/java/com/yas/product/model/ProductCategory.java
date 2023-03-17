package com.yas.product.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private int displayOrder;

    private boolean isFeaturedProduct;
}
