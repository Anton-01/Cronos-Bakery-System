package com.cronos.bakery.application.mapper;

import com.cronos.bakery.application.dto.request.EmailSettingsRequest;
import com.cronos.bakery.application.dto.response.EmailSettingsResponse;
import com.cronos.bakery.domain.entity.customization.EmailSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailSettingsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    EmailSettings toEntity(EmailSettingsRequest request);

    EmailSettingsResponse toResponse(EmailSettings emailSettings);
}
