package com.cryptomanager.controllers;

import com.cryptomanager.models.Portfolio;
import com.cryptomanager.services.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
            // Formata a resposta com uma mensagem descritiva
            String responseMessage = "O valor total do portfólio é: " + totalValue;
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            // Retorna uma resposta com status 404 e a mensagem de erro
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Retorna uma resposta genérica para outros erros
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.addPortfolio(portfolio);
        return ResponseEntity.ok("Portfólio adicionado ou atualizado com sucesso!");
    }
}
