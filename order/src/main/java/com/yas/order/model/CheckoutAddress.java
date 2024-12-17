package com.yas.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yas.order.viewmodel.enumeration.CheckoutAddressType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkout_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contactName;
    private String phone;
    private String addressLine1;
    private String city;
    private String zipCode;
    private Long districtId;
    private String districtName;
    private Long stateOrProvinceId;
    private String stateOrProvinceName;
    private String stateOrProvinceCode;
    private Long countryId;
    private String countryName;
    private String countryCode2;
    private String countryCode3;

    @Enumerated(EnumType.STRING)
    private CheckoutAddressType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", updatable = false, nullable = false)
    @JsonBackReference
    private Checkout checkout;
}
