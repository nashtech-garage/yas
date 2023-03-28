package com.yas.location.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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
  private List<StateOrProvince> stateOrProvinces = new ArrayList<>();
}
