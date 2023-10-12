package com.yas.product.model.attribute;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @ManyToOne
    @JoinColumn(name = "product_template_id", nullable = false)
    private ProductTemplate productTemplate;



}
