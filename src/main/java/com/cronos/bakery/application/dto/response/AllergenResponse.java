package com.cronos.bakery.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllergenResponse {
    private Long id;
    private String name;
    private String nameEn;
    private String nameEs;
    private String description;
    private Boolean isSystemDefault;
}
