package com.cronos.bakery.presentation.controller;

import com.cronos.bakery.application.dto.request.CreateQuoteRequest;
import com.cronos.bakery.application.dto.request.UpdateQuoteRequest;
import com.cronos.bakery.application.dto.response.*;
import com.cronos.bakery.application.dto.response.QuoteStatisticsResponse;
import com.cronos.bakery.domain.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
@Tag(name = "Quotes", description = "Quote management endpoints")
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create new quote")
    public ResponseEntity<ApiResponse<QuoteResponse>> createQuote(@Valid @RequestBody CreateQuoteRequest request, Authentication authentication) {

        QuoteResponse response = quoteService.createQuote(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quote created successfully", response));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update quote")
    public ResponseEntity<ApiResponse<QuoteResponse>> updateQuote(@PathVariable Long id, @Valid @RequestBody UpdateQuoteRequest request, Authentication authentication) {
        QuoteResponse response = quoteService.updateQuote(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quote updated successfully", response));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all quotes")
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getUserQuotes(Authentication authentication, Pageable pageable) {
        Page<QuoteResponse> quotes = quoteService.getUserQuotes(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(quotes));
    }

    @GetMapping("/statistics")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get quotes statistics")
    public ResponseEntity<ApiResponse<QuoteStatisticsResponse>> getStatistics(Authentication authentication) {
        QuoteStatisticsResponse statistics = quoteService.getStatistics(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @PostMapping("/{id}/share")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Generate shareable link")
    public ResponseEntity<ApiResponse<ShareQuoteResponse>> generateShareLink(@PathVariable Long id, Authentication authentication) {
        ShareQuoteResponse response = quoteService.generateShareLink(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Share link generated", response));
    }

    @GetMapping("/shared/{token}")
    @Operation(summary = "Get shared quote (public access)")
    public ResponseEntity<ApiResponse<SharedQuoteResponse>> getSharedQuote(@PathVariable String token, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        SharedQuoteResponse response = quoteService.getSharedQuote(token, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/send-email")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Send quote by email")
    public ResponseEntity<ApiResponse<Void>> sendQuoteByEmail(@PathVariable Long id, @RequestParam String recipientEmail, Authentication authentication) {
        quoteService.sendQuoteByEmail(id, recipientEmail, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Quote sent successfully", null));
    }

    @GetMapping("/{id}/access-stats")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get quote access statistics")
    public ResponseEntity<ApiResponse<QuoteAccessStatsResponse>> getAccessStats(@PathVariable Long id, Authentication authentication) {
        QuoteAccessStatsResponse response = quoteService.getQuoteAccessStats(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
