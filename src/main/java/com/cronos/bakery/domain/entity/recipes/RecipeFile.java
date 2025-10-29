package com.cronos.bakery.domain.entity.recipes;

import com.cronos.bakery.application.service.enums.FileType;
import com.cronos.bakery.domain.entity.core.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeFile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    private String description;
}
