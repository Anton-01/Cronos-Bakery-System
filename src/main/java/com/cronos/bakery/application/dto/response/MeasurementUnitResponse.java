package com.cronos.bakery.application.dto.response;

import lombok.*;

@Data
@Builder
public class MeasurementUnitResponse {
    private Long id;
    private String code;
    private String name;
    private String namePlural;
    private String type;
    private Boolean isSystemDefault;
}
