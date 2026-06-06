package com.currency.converter.controller;


import com.currency.converter.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "Currency Conversion Interface", description = "Endpoints for converting global financial values")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
    @GetMapping("/convertCurrency")
    @Operation(summary = "Convert currency from a base denomination to a target denomination using current rates")
    public ResponseEntity<Double> convertCurrency(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam double units) {

        log.info("Inbound API Gateway Request - Parameters -> From: {}, To: {}, Units: {}", fromCurrency, toCurrency, units);
        double convertedValue = currencyService.convert(fromCurrency, toCurrency, units);
        return ResponseEntity.ok(convertedValue);
    }
}

