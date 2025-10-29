package com.cronos.bakery.domain.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyExchangeService {

    private static final String EXCHANGE_API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Converts amount from one currency to another
     * Cache for 1 hour
     */
    @Cacheable(value = "exchangeRates", key = "#from + '_' + #to")
    public BigDecimal convert(BigDecimal amount, String from, String to) {

        if (from.equals(to)) {
            return amount;
        }

        try {
            BigDecimal rate = getExchangeRate(from, to);
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            log.error("Error converting currency from {} to {}: {}", from, to, e.getMessage());
            throw new RuntimeException("Currency conversion failed", e);
        }
    }

    /**
     * Gets exchange rate from API
     */
    private BigDecimal getExchangeRate(String from, String to) {
        String url = EXCHANGE_API_URL + from;
        String response = restTemplate.getForObject(url, String.class);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject rates = jsonObject.getAsJsonObject("rates");

        return rates.get(to).getAsBigDecimal();
    }

    /**
     * Gets current exchange rate without conversion
     */
    @Cacheable(value = "exchangeRates", key = "#from + '_' + #to + '_rate'")
    public BigDecimal getRate(String from, String to) {
        return getExchangeRate(from, to);
    }
}
