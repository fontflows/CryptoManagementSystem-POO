package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/total-value")
    public double calculateTotalValue(@RequestParam String userId, @RequestParam String portfolioId) {
        return portfolioService.calculateTotalValue(userId, portfolioId);
    }

    @PostMapping("/add")
    public String addPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.addPortfolio(portfolio);
        return "Portfólio adicionado ou atualizado com sucesso!";
    }
}
