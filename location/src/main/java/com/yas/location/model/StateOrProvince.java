package com.yas.location.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "state_or_province")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateOrProvince {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
}
