package com.cronos.bakery.application.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class RecipeVersionResponse {
    private Long id;
    private Integer versionNumber;
    private String versionName;
    private String changes;
    private LocalDateTime createdAt;
    private String createdBy;
    private Boolean isCurrent;
}
