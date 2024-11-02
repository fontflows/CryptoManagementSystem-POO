package com.cryptomanager.controllers;

import com.cryptomanager.models.*;
import com.cryptomanager.services.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/total-value")
    public ResponseEntity<String> calculateTotalValue(@RequestParam String userId, @RequestParam String portfolioId) {
        try {
            double totalValue = portfolioService.calculateTotalValue(userId, portfolioId);
            String responseMessage = "O valor total do portfólio é: " + totalValue;
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.addPortfolio(portfolio);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");
    }

    @GetMapping("/get-suggested-crypto")
    public ResponseEntity<CryptoCurrency> suggestCryptoCurrency(@RequestParam String userID, @RequestParam String portfolioID){
        try {
            return ResponseEntity.ok(portfolioService.suggestCryptoCurrency(userID, portfolioID));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/set-Investment-Strategy")
    public ResponseEntity<String> setPortfolioInvestmentStrategy(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam StrategyNames strategyName) {
        portfolioService.setPortfolioInvestmentStrategy(userID, portfolioID, strategyName.getDisplayName());
        return ResponseEntity.ok("Estratégia de investimento atualizada com sucesso!");
    }
}
