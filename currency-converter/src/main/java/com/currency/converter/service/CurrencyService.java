package com.currency.converter.service;

import com.currency.converter.entity.ConversionAudit;
import com.currency.converter.repository.ConversionAuditRepository;
import lombok.RequiredArgsConstructor; // <-- Switched to RequiredArgsConstructor
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor // <-- Generates a constructor ONLY for fields marked 'final'
@Getter
@Setter
public class CurrencyService {

    // Spring will successfully find and inject these two beans
    private final RestTemplate restTemplate;
    private final ConversionAuditRepository repository;

    // Spring will inject these directly from your application.properties file after construction
    @Value("${freecurrencyapi.api.key}")
    private String apiKey;

    @Value("${freecurrencyapi.base.url}")
    private String baseUrl;

    public double convert(String fromCurrency, String toCurrency, double units) {
        log.info("Converting from {} to {}", fromCurrency, toCurrency);

        String url = String.format("%s?apikey=%s&base_currency=%s&currencies=%s",
                baseUrl, apiKey, fromCurrency.toUpperCase(), toCurrency.toUpperCase());

        try {
            log.debug("Dispatching external request to API endpoint: {}", baseUrl);
            Map response = restTemplate.getForObject(url, Map.class);

            Map<String, Object> data = (Map<String, Object>) response.get("data");

            double exchangeRate = Double.parseDouble(data.get(toCurrency.toUpperCase()).toString());
            double finalResult = units * exchangeRate;
            log.info("Successfully fetched market rates. Conversion factor: {}", exchangeRate);

            ConversionAudit auditRecord = new ConversionAudit();
            auditRecord.setFromCurrency(fromCurrency.toUpperCase());
            auditRecord.setToCurrency(toCurrency.toUpperCase());
            auditRecord.setUnits(units);
            auditRecord.setResult(finalResult);

            repository.save(auditRecord);
            log.info("Auditing data successfully persisted into MySQL database schema.");

            return finalResult;

        } catch (Exception e) {
            log.error("An error occurred executing currency exchange operation: {}", e.getMessage(), e);
            throw new RuntimeException("Currency calculation failed due to backend exception.");
        }
    }
}
