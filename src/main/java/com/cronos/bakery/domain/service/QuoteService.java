package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.recipes.RecipeCostCalculation;
import com.cronos.bakery.application.dto.request.CreateQuoteRequest;
import com.cronos.bakery.application.dto.request.QuoteItemRequest;
import com.cronos.bakery.application.dto.request.UpdateQuoteRequest;
import com.cronos.bakery.application.dto.response.*;
import com.cronos.bakery.application.dto.response.QuoteStatisticsResponse;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.quote.Quote;
import com.cronos.bakery.domain.entity.quote.QuoteAccessLog;
import com.cronos.bakery.domain.entity.quote.QuoteItem;
import com.cronos.bakery.domain.entity.quote.enums.QuoteStatus;
import com.cronos.bakery.domain.entity.recipes.ProfitMargin;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.cronos.bakery.infrastructure.config.AppProperties;
import com.cronos.bakery.infrastructure.constants.ApplicationConstants;
import com.cronos.bakery.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteAccessLogRepository accessLogRepository;
    private final RecipeRepository recipeRepository;
    private final ProfitMarginRepository profitMarginRepository;
    private final UserRepository userRepository;
    private final RecipeCostCalculationService costCalculationService;
    private final PdfGenerationService pdfGenerationService;
    private final EmailService emailService;
    private final AppProperties appProperties;

    /**
     * Creates a new quote
     */
    @Transactional
    public QuoteResponse createQuote(CreateQuoteRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quote quote = Quote.builder()
                .user(user)
                .clientName(request.getClientName())
                .clientEmail(request.getClientEmail())
                .clientPhone(request.getClientPhone())
                .clientAddress(request.getClientAddress())
                .notes(request.getNotes())
                .status(QuoteStatus.DRAFT)
                .currency(user.getDefaultCurrency())
                .taxRate(user.getDefaultTaxRate())
                .validUntil(LocalDateTime.now().plusDays(request.getValidityDays() != null ?
                        request.getValidityDays() : ApplicationConstants.DEFAULT_QUOTE_VALIDITY_DAYS))
                .build();

        // Add items
        Set<QuoteItem> items = new HashSet<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (QuoteItemRequest itemRequest : request.getItems()) {
            Recipe recipe = recipeRepository.findById(itemRequest.getRecipeId())
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));

            ProfitMargin profitMargin = profitMarginRepository.findById(itemRequest.getProfitMarginId())
                    .orElseThrow(() -> new RuntimeException("Profit margin not found"));

            // Calculate costs
            RecipeCostCalculation costCalc = costCalculationService.calculateRecipeCost(
                    recipe,
                    itemRequest.getScaleFactor() != null ? itemRequest.getScaleFactor() : BigDecimal.ONE,
                    user
            );

            // Calculate price with profit margin
            BigDecimal unitPrice = calculatePriceWithMargin(
                    costCalc.getCostPerUnit(),
                    profitMargin.getPercentage()
            );

            BigDecimal itemSubtotal = unitPrice.multiply(itemRequest.getQuantity())
                    .setScale(2, RoundingMode.HALF_UP);

            QuoteItem item = QuoteItem.builder()
                    .quote(quote)
                    .recipe(recipe)
                    .quantity(itemRequest.getQuantity())
                    .scaleFactor(itemRequest.getScaleFactor())
                    .profitMargin(profitMargin)
                    .unitCost(costCalc.getCostPerUnit())
                    .profitPercentage(profitMargin.getPercentage())
                    .unitPrice(unitPrice)
                    .subtotal(itemSubtotal)
                    .notes(itemRequest.getNotes())
                    .displayOrder(itemRequest.getDisplayOrder())
                    .build();

            items.add(item);
            subtotal = subtotal.add(itemSubtotal);
        }

        quote.setItems(items);
        quote.setSubtotal(subtotal);

        // Calculate tax
        BigDecimal taxAmount = subtotal.multiply(quote.getTaxRate())
                .divide(
                        BigDecimal.valueOf(ApplicationConstants.PERCENTAGE_BASE),
                        ApplicationConstants.MONETARY_CALCULATION_SCALE,
                        ApplicationConstants.DEFAULT_ROUNDING_MODE
                );
        quote.setTaxAmount(taxAmount);

        // Calculate total
        BigDecimal total = subtotal.add(taxAmount);
        quote.setTotal(total);

        quote = quoteRepository.save(quote);

        log.info("Quote created: {} for user: {}", quote.getQuoteNumber(), username);

        return mapToResponse(quote);
    }

    /**
     * Updates an existing quote
     */
    @Transactional
    public QuoteResponse updateQuote(Long quoteId, UpdateQuoteRequest request, String username) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateQuoteOwnership(quote, username);

        if (request.getClientName() != null) quote.setClientName(request.getClientName());
        if (request.getClientEmail() != null) quote.setClientEmail(request.getClientEmail());
        if (request.getClientPhone() != null) quote.setClientPhone(request.getClientPhone());
        if (request.getClientAddress() != null) quote.setClientAddress(request.getClientAddress());
        if (request.getNotes() != null) quote.setNotes(request.getNotes());
        if (request.getStatus() != null) quote.setStatus(request.getStatus());

        quote = quoteRepository.save(quote);

        return mapToResponse(quote);
    }

    /**
     * Generates a shareable link for a quote
     */
    @Transactional
    public ShareQuoteResponse generateShareLink(Long quoteId, String username) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateQuoteOwnership(quote, username);

        quote.generateShareToken();
        quote = quoteRepository.save(quote);

        String shareUrl = String.format("%s/shared-quotes/%s", appProperties.getBaseUrl(), quote.getShareToken());

        log.info("Share link generated for quote: {}", quote.getQuoteNumber());

        return ShareQuoteResponse.builder()
                .shareToken(quote.getShareToken())
                .shareUrl(shareUrl)
                .expiresAt(quote.getShareExpiresAt())
                .build();
    }

    /**
     * Gets a quote by share token (public access)
     */
    @Transactional
    public SharedQuoteResponse getSharedQuote(String token, String ipAddress, String userAgent) {
        Quote quote = quoteRepository.findValidSharedQuote(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Quote not found or expired"));

        // Log access
        QuoteAccessLog accessLog = QuoteAccessLog.builder()
                .quote(quote)
                .accessedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        accessLogRepository.save(accessLog);

        // Update quote status if first view
        if (quote.getStatus() == QuoteStatus.SENT) {
            quote.setStatus(QuoteStatus.VIEWED);
            quoteRepository.save(quote);
        }

        log.info("Shared quote accessed: {} from IP: {}", quote.getQuoteNumber(), ipAddress);

        return mapToSharedResponse(quote);
    }

    /**
     * Sends quote by email
     */
    @Transactional
    public void sendQuoteByEmail(Long quoteId, String recipientEmail, String username) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateQuoteOwnership(quote, username);

        // Generate PDF
        byte[] pdfContent = pdfGenerationService.generateQuotePdf(quote);

        // Send email
        emailService.sendQuoteEmail(quote, recipientEmail, pdfContent);

        // Update status
        if (quote.getStatus() == QuoteStatus.DRAFT) {
            quote.setStatus(QuoteStatus.SENT);
            quoteRepository.save(quote);
        }

        log.info("Quote {} sent to: {}", quote.getQuoteNumber(), recipientEmail);
    }

    /**
     * Gets all quotes for a user
     */
    @Transactional(readOnly = true)
    public Page<QuoteResponse> getUserQuotes(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return quoteRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets quote access logs
     */
    @Transactional(readOnly = true)
    public QuoteAccessStatsResponse getQuoteAccessStats(Long quoteId, String username) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        validateQuoteOwnership(quote, username);

        long totalAccesses = accessLogRepository.countAccessesByQuote(quote);
        var recentLogs = accessLogRepository.findRecentAccessLogs(
                quote,
                LocalDateTime.now().minusDays(ApplicationConstants.RECENT_ACCESS_DAYS)
        );

        return QuoteAccessStatsResponse.builder()
                .quoteId(quoteId)
                .totalAccesses(totalAccesses)
                .recentAccesses(recentLogs.stream()
                        .map(log -> AccessLogEntry.builder()
                                .accessedAt(log.getAccessedAt())
                                .ipAddress(log.getIpAddress())
                                .userAgent(log.getUserAgent())
                                .build())
                        .toList())
                .build();
    }

    private BigDecimal calculatePriceWithMargin(BigDecimal cost, BigDecimal marginPercentage) {
        BigDecimal multiplier = BigDecimal.ONE.add(
                marginPercentage.divide(
                        BigDecimal.valueOf(ApplicationConstants.PERCENTAGE_BASE),
                        ApplicationConstants.PERCENTAGE_CALCULATION_SCALE,
                        ApplicationConstants.DEFAULT_ROUNDING_MODE
                )
        );
        return cost.multiply(multiplier).setScale(
                ApplicationConstants.MONETARY_CALCULATION_SCALE,
                ApplicationConstants.DEFAULT_ROUNDING_MODE
        );
    }

    private void validateQuoteOwnership(Quote quote, String username) {
        if (!quote.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to quote");
        }
    }

    private QuoteResponse mapToResponse(Quote quote) {
        return QuoteResponse.builder()
                .id(quote.getId())
                .quoteNumber(quote.getQuoteNumber())
                .clientName(quote.getClientName())
                .clientEmail(quote.getClientEmail())
                .status(quote.getStatus())
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .currency(quote.getCurrency())
                .validUntil(quote.getValidUntil())
                .createdAt(quote.getCreatedAt())
                .build();
    }

    private SharedQuoteResponse mapToSharedResponse(Quote quote) {
        return SharedQuoteResponse.builder()
                .quoteNumber(quote.getQuoteNumber())
                .businessName(quote.getUser().getBusinessName())
                .clientName(quote.getClientName())
                .subtotal(quote.getSubtotal())
                .taxRate(quote.getTaxRate())
                .taxAmount(quote.getTaxAmount())
                .total(quote.getTotal())
                .currency(quote.getCurrency())
                .validUntil(quote.getValidUntil())
                .items(quote.getItems().stream()
                        .map(item -> SharedQuoteItemResponse.builder()
                                .recipeName(item.getRecipe().getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Gets statistics for quotes
     */
    @Transactional(readOnly = true)
    public QuoteStatisticsResponse getStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalQuotes = quoteRepository.countByUser(user);
        long draftQuotes = quoteRepository.countByUserAndStatus(user, QuoteStatus.DRAFT);
        long sentQuotes = quoteRepository.countByUserAndStatus(user, QuoteStatus.SENT);
        long viewedQuotes = quoteRepository.countByUserAndStatus(user, QuoteStatus.VIEWED);
        long acceptedQuotes = quoteRepository.countByUserAndStatus(user, QuoteStatus.ACCEPTED);
        long rejectedQuotes = quoteRepository.countByUserAndStatus(user, QuoteStatus.REJECTED);

        BigDecimal totalQuotedValue = quoteRepository.calculateTotalQuotedValue(user);
        if (totalQuotedValue == null) {
            totalQuotedValue = BigDecimal.ZERO;
        }

        BigDecimal totalAcceptedValue = quoteRepository.calculateTotalValueByStatus(user, QuoteStatus.ACCEPTED);
        if (totalAcceptedValue == null) {
            totalAcceptedValue = BigDecimal.ZERO;
        }

        // Calculate conversion rate
        Double conversionRate = totalQuotes > 0 ?
                (acceptedQuotes * 100.0 / totalQuotes) : 0.0;

        // Count active quotes (not rejected, not expired)
        long activeQuotes = quoteRepository.countActiveQuotes(
                user,
                LocalDateTime.now(),
                List.of(QuoteStatus.REJECTED, QuoteStatus.EXPIRED)
        );

        return QuoteStatisticsResponse.builder()
                .totalQuotes(totalQuotes)
                .draftQuotes(draftQuotes)
                .sentQuotes(sentQuotes)
                .viewedQuotes(viewedQuotes)
                .acceptedQuotes(acceptedQuotes)
                .rejectedQuotes(rejectedQuotes)
                .totalQuotedValue(totalQuotedValue.setScale(2, RoundingMode.HALF_UP))
                .totalAcceptedValue(totalAcceptedValue.setScale(2, RoundingMode.HALF_UP))
                .currency(user.getDefaultCurrency())
                .conversionRate(conversionRate)
                .activeQuotes(activeQuotes)
                .build();
    }
}
