package com.cronos.bakery.domain.entity.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "allergens")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergen extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_es")
    private String nameEs;

    private String description;

    @Column(name = "is_system_default")
    @Builder.Default
    private Boolean isSystemDefault = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
