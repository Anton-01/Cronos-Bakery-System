package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.CreateConversionFactorRequest;
import com.cronos.bakery.application.dto.request.UnitConversionRequest;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.ConversionFactorResponse;
import com.cronos.bakery.application.dto.response.MeasurementUnitResponse;
import com.cronos.bakery.application.dto.response.UnitConversionResponse;
import com.cronos.bakery.application.service.ConversionFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/conversions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Unit Conversions", description = "Unit conversion endpoints")
public class ConversionController {

    private final ConversionFactorService conversionFactorService;

    @PostMapping
    @Operation(summary = "Create custom conversion factor")
    public ResponseEntity<ApiResponse<ConversionFactorResponse>> createConversionFactor(@Valid @RequestBody CreateConversionFactorRequest request, Authentication authentication) {

        ConversionFactorResponse response = conversionFactorService.createConversionFactor(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversion factor created", response));
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert units")
    public ResponseEntity<ApiResponse<UnitConversionResponse>> convertUnits(@Valid @RequestBody UnitConversionRequest request, Authentication authentication) {
        UnitConversionResponse response = conversionFactorService.convertUnits(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all conversion factors")
    public ResponseEntity<ApiResponse<List<ConversionFactorResponse>>> getUserConversions(Authentication authentication) {
        List<ConversionFactorResponse> conversions = conversionFactorService.getUserConversions(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(conversions));
    }

    @GetMapping("/units")
    @Operation(summary = "Get all measurement units")
    public ResponseEntity<ApiResponse<List<MeasurementUnitResponse>>> getAllUnits() {
        List<MeasurementUnitResponse> units = conversionFactorService.getAllUnits();
        return ResponseEntity.ok(ApiResponse.success(units));
    }
}
