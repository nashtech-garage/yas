package com.yas.cart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import com.yas.commonlibrary.model.AbstractAuditEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@IdClass(CartItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Builder
public class CartItem extends AbstractAuditEntity {
    @Id
    private String customerId;
    @Id
    private Long productId;
    private int quantity;
}