package com.cryptomanager.controllers;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.LoginRepository;
import com.cryptomanager.services.CurrencyConverterService;
import com.cryptomanager.services.PortfolioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RestController
@RequestMapping("3/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final CurrencyConverterService currencyConverterService;
    private final LoginRepository loginRepository;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, CurrencyConverterService currencyConverterService, LoginRepository loginRepository) {
        this.portfolioService = portfolioService;
        this.currencyConverterService = currencyConverterService;
        this.loginRepository = loginRepository;
    }

    @GetMapping("/total-value")
    public ResponseEntity<String> calculateTotalValue() {
        try {
            double totalValue = portfolioService.calculateTotalValue(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1]);
            String responseMessage = "O valor total do portfólio é: " + totalValue;
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }

    @PostMapping("/crypto-conversion-by-portfolioId")
    public ResponseEntity<String> convertCrypto(@RequestParam String fromCryptoName, @RequestParam String toCryptoName, @RequestParam double balance) throws IOException{
        try {
            currencyConverterService.currencyConverter(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], fromCryptoName, toCryptoName, balance);
            return ResponseEntity.ok("Criptomoeda convertida com sucesso !");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao converter criptomoeda com o saldo informado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }
  
    @GetMapping("/get-suggested-crypto")
    public ResponseEntity<?> suggestCryptoCurrency(){
        try {
            return ResponseEntity.ok(portfolioService.suggestCryptoCurrency(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1]));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor ao sugerir criptomoeda" + e.getMessage());
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao sugerir criptomoeda: " + e.getMessage());
        }
    }

    @PostMapping("/set-Investment-Strategy")
    public ResponseEntity<String> setPortfolioInvestmentStrategy(@RequestParam StrategyNames strategyName) {
        try {
            portfolioService.setPortfolioInvestmentStrategy(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], strategyName.getDisplayName());
            return ResponseEntity.ok("Estratégia de investimento atualizada com sucesso!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor ao atualizar estratégia: " + e.getMessage());
        }
    }

    @PostMapping("/add-balance")
    public ResponseEntity<String> addBalance(@RequestParam double amount){
        try{
            portfolioService.addBalance(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], amount);
            return ResponseEntity.ok("Saldo adicionado com sucesso!");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao adicionar saldo: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/redeem-balance")
    public ResponseEntity<String> redeemBalance(@RequestParam double amount){
        try{
            portfolioService.redeemBalance(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], amount);
            return ResponseEntity.ok("Saldo resgatado com sucesso!");
        } catch(IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao resgatar saldo: " + e.getMessage());
        }
    }
  
    @PostMapping("/buy-crypto")
    public ResponseEntity<String> buyCrypto(@RequestParam String cryptoName, @RequestParam double amount){
        try{
            portfolioService.buyCrypto(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda comprada com sucesso!");
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao comprar criptomoeda: " + e.getMessage());
        }
    }

    @PostMapping("/sell-crypto")
    public ResponseEntity<String> sellCrypto(@RequestParam String cryptoName, @RequestParam double amount){
        try{
            portfolioService.sellCrypto(loginRepository.loadLoggedInfo()[0], loginRepository.loadLoggedInfo()[1], cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda vendida com sucesso!");
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao vender criptomoeda: " + e.getMessage());
        }
    }
}
