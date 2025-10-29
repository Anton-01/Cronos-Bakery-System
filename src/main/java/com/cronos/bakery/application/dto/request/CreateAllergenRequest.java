package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAllergenRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    private String nameEn;

    private String nameEs;

    private String description;
}
