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
import jakarta.persistence.OneToOne;
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
public class CheckoutAddress{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    private String contactName;

    @SuppressWarnings("unused")
    private String phone;

    @SuppressWarnings("unused")
    private String addressLine1;

    @SuppressWarnings("unused")
    private String city;

    @SuppressWarnings("unused")
    private String zipCode;

    @SuppressWarnings("unused")
    private Long districtId;

    @SuppressWarnings("unused")
    private String districtName;

    @SuppressWarnings("unused")
    private Long stateOrProvinceId;

    @SuppressWarnings("unused")
    private String stateOrProvinceName;

    @SuppressWarnings("unused")
    private String stateOrProvinceCode;

    @SuppressWarnings("unused")
    private Long countryId;

    @SuppressWarnings("unused")
    private String countryName;

    @SuppressWarnings("unused")
    private String countryCode2;

    @SuppressWarnings("unused")
    private String countryCode3;

    @Enumerated(EnumType.STRING)
    private CheckoutAddressType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkout_id", updatable = false, nullable = false)
    @JsonBackReference
    private Checkout checkout;
}
