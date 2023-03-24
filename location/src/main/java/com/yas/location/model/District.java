package com.yas.location.model;

import com.yas.location.model.StateOrProvince;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Entity
@Table(name = "district")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 450)
    private String name;

    @Column(length = 450)
    private String type;

    private String location;

    @ManyToOne
    @JoinColumn(name = "state_or_province_id", nullable = false)
    private StateOrProvince stateOrProvince;
}

