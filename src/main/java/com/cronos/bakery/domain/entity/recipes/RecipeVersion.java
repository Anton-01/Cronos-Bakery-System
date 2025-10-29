package com.cronos.bakery.domain.entity.recipes;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_versions", indexes = {
        @Index(name = "idx_recipe_version", columnList = "recipe_id, version_number")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "version_name")
    private String versionName;

    @Column(length = 5000)
    private String changes;

    @Column(name = "snapshot_data", columnDefinition = "TEXT")
    private String snapshotData; // JSON snapshot of the recipe at this version

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = false;
}
