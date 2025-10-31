package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.notifications.PriceChangeNotification;
import com.cronos.bakery.domain.entity.core.MaterialPriceHistory;
import com.cronos.bakery.domain.entity.core.RawMaterial;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.infrastructure.persistence.RawMaterialRepository;
import com.cronos.bakery.infrastructure.persistence.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceChangeNotificationService {

    private final RecipeRepository recipeRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final EmailService emailService;
    private final WebSocketNotificationService webSocketService;

    /**
     * Handles price change for a raw material
     */
    @Transactional
    public void handlePriceChange(RawMaterial material, BigDecimal newPrice, String reason) {

        BigDecimal oldPrice = material.getUnitCost();

        // Calculate percentage change
        BigDecimal changePercentage = calculatePercentageChange(oldPrice, newPrice);

        // Update material price
        material.setUnitCost(newPrice);
        material.setLastPriceUpdate(LocalDateTime.now());
        material.setNeedsRecalculation(true);

        // Create price history record
        MaterialPriceHistory history = MaterialPriceHistory.builder()
                .rawMaterial(material)
                .previousCost(oldPrice)
                .newCost(newPrice)
                .changePercentage(changePercentage)
                .changedAt(LocalDateTime.now())
                .reason(reason)
                .build();

        material.getPriceHistory().add(history);
        rawMaterialRepository.save(material);

        // Find affected recipes
        List<Recipe> affectedRecipes = recipeRepository.findRecipesUsingMaterial(material.getId());

        // Mark recipes as needing recalculation
        for (Recipe recipe : affectedRecipes) {
            recipe.setNeedsRecalculation(true);
        }

        recipeRepository.saveAll(affectedRecipes);

        // Send notifications
        sendPriceChangeNotifications(material, oldPrice, newPrice, changePercentage, affectedRecipes);

        log.info("Price change handled for material: {} - Old: {}, New: {}, Affected recipes: {}",
                material.getName(), oldPrice, newPrice, affectedRecipes.size());
    }

    private BigDecimal calculatePercentageChange(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        return newPrice.subtract(oldPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private void sendPriceChangeNotifications(RawMaterial material, BigDecimal oldPrice,
                                              BigDecimal newPrice, BigDecimal changePercentage,
                                              List<Recipe> affectedRecipes) {

        User user = material.getUser();

        // Email notification
        PriceChangeEmailData emailData = PriceChangeEmailData.builder()
                .materialName(material.getName())
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changePercentage(changePercentage)
                .affectedRecipesCount(affectedRecipes.size())
                .affectedRecipes(affectedRecipes.stream()
                        .map(Recipe::getName)
                        .limit(10)
                        .toList())
                .build();

        emailService.sendPriceChangeNotification(user, emailData);

        // WebSocket notification (real-time dashboard update)
        PriceChangeNotification wsNotification = PriceChangeNotification.builder()
                .materialId(material.getId())
                .materialName(material.getName())
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changePercentage(changePercentage)
                .affectedRecipesCount(affectedRecipes.size())
                .timestamp(LocalDateTime.now())
                .build();

        webSocketService.sendToUser(user.getUsername(), "/topic/price-changes", wsNotification);
    }
}
