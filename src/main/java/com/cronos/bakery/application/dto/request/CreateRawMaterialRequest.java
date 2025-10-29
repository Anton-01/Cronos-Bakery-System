package com.cronos.bakery.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRawMaterialRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Supplier is required")
    private String supplier;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Purchase unit is required")
    private Long purchaseUnitId;

    @NotNull(message = "Purchase quantity is required")
    @DecimalMin(value = "0.0001", message = "Purchase quantity must be greater than 0")
    private BigDecimal purchaseQuantity;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.01", message = "Unit cost must be greater than 0")
    private BigDecimal unitCost;

    private String currency;

    private BigDecimal currentStock;

    private BigDecimal minimumStock;

    private Set<Long> allergenIds;
}
