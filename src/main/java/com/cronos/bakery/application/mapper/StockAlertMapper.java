package com.cronos.bakery.application.mapper;

import com.cronos.bakery.application.dto.response.StockAlertResponse;
import com.cronos.bakery.domain.entity.inventory.StockAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockAlertMapper {

    @Mapping(target = "rawMaterialId", source = "rawMaterial.id")
    @Mapping(target = "rawMaterialName", source = "rawMaterial.name")
    @Mapping(target = "rawMaterialUnit", source = "rawMaterial.unit")
    StockAlertResponse toResponse(StockAlert stockAlert);
}
