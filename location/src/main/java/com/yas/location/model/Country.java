package com.yas.location.model;

import java.util.ArrayList;
import java.util.List;
import com.yas.location.model.StateOrProvince;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 450)
    private String name;

    @Column(nullable = false, length = 450)
    private String code3;

    private Boolean isBillingEnabled;
    private Boolean isShippingEnabled;
    private Boolean isCityEnabled;
    private Boolean isZipCodeEnabled;
    private Boolean isDistrictEnabled;

    @OneToMany(mappedBy = "country")
    private List<StateOrProvince> stateOrProvinces = new ArrayList<>();;
}

