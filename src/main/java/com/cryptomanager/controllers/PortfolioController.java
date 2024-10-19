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
    public double calculateTotalValue(@RequestBody Portfolio portfolio) {
        return portfolioService.calculateTotalValue(portfolio);
    }

    @PostMapping("/add")
    public String addPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.addPortfolio(portfolio);
        return "Portfólio adicionado ou atualizado com sucesso!";
    }
}
