package com.cronos.bakery.infrastructure.util;

import java.math.BigDecimal;

import static com.cronos.bakery.infrastructure.constants.ApplicationConstants.*;

public class PercentageUtils {

    /**
     * Calcula el porcentaje (currentQuantity / thresholdQuantity) * 100.
     * Asegura cálculos precisos utilizando BigDecimal con un redondeo y escala predefinidos.
     *
     * @param currentQuantity El valor actual (numerador).
     * @param thresholdQuantity El valor umbral o total (denominador).
     * @return Un BigDecimal que representa el porcentaje calculado.
     */
    public static BigDecimal calculatePercentage(BigDecimal currentQuantity, BigDecimal thresholdQuantity) {

        if (thresholdQuantity == null || thresholdQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal resultOfDivision = currentQuantity.divide(
                thresholdQuantity,
                DEFAULT_DIVISION_SCALE,
                DEFAULT_ROUNDING_MODE
        );

        return resultOfDivision.multiply(HUNDRED);
    }
}
