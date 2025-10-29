package com.cronos.bakery.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "measurement_units")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementUnit extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // kg, g, l, ml, pcs, bag, bottle, etc.

    @Column(nullable = false)
    private String name;

    @Column(name = "name_plural")
    private String namePlural;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType type; // WEIGHT, VOLUME, PIECE, CONTAINER

    @Column(name = "is_system_default")
    @Builder.Default
    private Boolean isSystemDefault = true;

    private String description;
}
