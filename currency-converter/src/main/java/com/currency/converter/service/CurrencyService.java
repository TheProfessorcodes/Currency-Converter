package com.currency.converter.service;

import com.currency.converter.entity.ConversionAudit;
import com.currency.converter.repository.ConversionAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final ConversionAuditRepository repository;

    @Value("${freecurrencyapi.api.key}")
    private String apiKey;

    @Value("${freecurrencyapi.base.url}")
    private String baseUrl;

    public double convert(String fromCurrency, String toCurrency, double units) {
        log.info("Converting from {} to {}", fromCurrency, toCurrency);

        try {
            log.debug("Dispatching external request using USD base to bypass free-tier plan restrictions");

            // Fixed: Only one URL variable declaration here, using locked USD base
            String url = String.format("%s?apikey=%s&base_currency=USD&currencies=%s,%s",
                    baseUrl, apiKey, fromCurrency.toUpperCase(), toCurrency.toUpperCase());

            Map response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> data = (Map<String, Object>) response.get("data");

            // Extract both rates relative to 1 USD
            double rateFromBaseUSD = Double.parseDouble(data.get(fromCurrency.toUpperCase()).toString());
            double rateToBaseUSD = Double.parseDouble(data.get(toCurrency.toUpperCase()).toString());

            // Compute cross-exchange multiplier rate manually (e.g., PKR to INR)
            double exchangeRate = rateToBaseUSD / rateFromBaseUSD;
            double finalResult = units * exchangeRate;

            log.info("Cross-rate calculated successfully: 1 {} = {} {}", fromCurrency, exchangeRate, toCurrency);

            // Persist to your database audit log
            ConversionAudit auditRecord = new ConversionAudit();
            auditRecord.setFromCurrency(fromCurrency.toUpperCase());
            auditRecord.setToCurrency(toCurrency.toUpperCase());
            auditRecord.setUnits(units);
            auditRecord.setResult(finalResult);

            repository.save(auditRecord);
            log.info("Auditing data successfully persisted into MySQL.");

            return finalResult;

        } catch (Exception e) {
            log.error("An error occurred executing currency exchange operation: {}", e.getMessage(), e);
            throw new RuntimeException("Currency calculation failed due to backend exception.");
        }
    }
}
