package com.cronos.bakery.application.service.enums;

public enum CostCalculationMethod {
    FIXED_AMOUNT,           // Monto fijo por receta
    TIME_BASED,            // Basado en tiempo de preparación
    PERCENTAGE_OF_MATERIAL, // Porcentaje del costo de materiales
    PER_UNIT               // Por unidad producida
}
