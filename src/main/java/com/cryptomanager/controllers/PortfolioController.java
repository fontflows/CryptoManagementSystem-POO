package com.cryptomanager.controllers;

import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.services.CurrencyConverterService;
import com.cryptomanager.services.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final CurrencyConverterService currencyConverterService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, CurrencyConverterService currencyConverterService) {
        this.portfolioService = portfolioService;
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping("/total-value")
    public ResponseEntity<String> calculateTotalValue(@RequestParam String userId, @RequestParam String portfolioId) {
        try {
            double totalValue = portfolioService.calculateTotalValue(userId, portfolioId);
            String responseMessage = "O valor total do portfólio é: " + totalValue;
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular o valor total do portfólio");
        }
    }

    @PostMapping("/crypto-conversion-by-portfolioId")
    public ResponseEntity<String> convertCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String fromCryptoName, @RequestParam String toCryptoName, @RequestParam double balance) throws Throwable {
        try {
            currencyConverterService.currencyConverter(userId, portfolioId, fromCryptoName, toCryptoName, balance);
            return ResponseEntity.ok("Criptomoeda convertida com sucesso!");
        } catch (IllegalArgumentException | NoSuchElementException | IOException e) {
            // Se ocorrer algum erro específico, ele será tratado pelo @ControllerAdvice
            throw e;
        } catch (Exception e) {
            // Qualquer outra exceção será tratada pelo handler genérico
            throw new RuntimeException("Erro ao converter criptomoeda", e);
        }
    }

    @GetMapping("/get-suggested-crypto")
    public ResponseEntity<?> suggestCryptoCurrency(@RequestParam String userID, @RequestParam String portfolioID) {
        try {
            return ResponseEntity.ok(portfolioService.suggestCryptoCurrency(userID, portfolioID));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao sugerir criptomoedas");
        }
    }

    @PostMapping("/set-Investment-Strategy")
    public ResponseEntity<String> setPortfolioInvestmentStrategy(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam StrategyNames strategyName) {
        try {
            portfolioService.setPortfolioInvestmentStrategy(userID, portfolioID, strategyName.getDisplayName());
            return ResponseEntity.ok("Estratégia de investimento atualizada com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar a estratégia de investimento");
        }
    }

    @PostMapping("/add-balance")
    public ResponseEntity<String> addBalance(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam double amount)  {
        try {
            portfolioService.addBalance(userID, portfolioID, amount);
            return ResponseEntity.ok("Saldo adicionado com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao adicionar saldo");
        }
    }

    @PostMapping("/redeem-balance")
    public ResponseEntity<String> redeemBalance(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam double amount) {
        try {
            portfolioService.redeemBalance(userID, portfolioID, amount);
            return ResponseEntity.ok("Saldo resgatado com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao resgatar saldo");
        }
    }

    @PostMapping("/buy-crypto")
    public ResponseEntity<String> buyCrypto(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.buyCrypto(userID, portfolioID, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda comprada com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comprar criptomoeda");
        }
    }

    @PostMapping("/sell-crypto")
    public ResponseEntity<String> sellCrypto(@RequestParam String userID, @RequestParam String portfolioID, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.sellCrypto(userID, portfolioID, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda vendida com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao vender criptomoeda");
        }
    }
}