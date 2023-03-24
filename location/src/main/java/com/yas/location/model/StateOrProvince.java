package com.yas.location.model;

import com.yas.location.model.Country;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "state_or_province")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateOrProvince extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 450)
    private String code;

    @Column(nullable = false, length = 450)
    private String name;

    @Column(length = 450)
    private String type;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}
