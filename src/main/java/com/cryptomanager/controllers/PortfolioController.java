package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.PortfolioNotFoundException;
import com.cryptomanager.models.StrategyNames;
import com.cryptomanager.services.CurrencyConverterService;
import com.cryptomanager.services.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Classe responsável por lidar com a manipulação das funcionalidades do portfolio de investimento dos usuários.
 */

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

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @return Mensagem de retorno da correta execução das funções associadas à captura do valor total.
     */
    @GetMapping("/total-value")
    public ResponseEntity<String> calculateTotalValue(@RequestParam String userId, @RequestParam String portfolioId) {
        try {
            double totalValue = portfolioService.calculateTotalValue(userId, portfolioId);
            String responseMessage = "O valor total do portfólio é: " + totalValue;
            return ResponseEntity.ok(responseMessage);
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param fromCryptoName Recebe o nome da critpomoeda a ser convertida.
     * @param toCryptoName Recebe o nome da critpomoeda de interesse do usuário a ser obtida.
     * @param balance Recebe o saldo que o usuário deseja converter.
     * @return Mensagem de retorno da correta execução das funções associadas à conversão do saldo.
     */
    @PostMapping("/crypto-conversion-by-portfolioId")
    public ResponseEntity<String> convertCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String fromCryptoName, @RequestParam String toCryptoName, @RequestParam double balance) {
        try {
            currencyConverterService.currencyConverter(userId, portfolioId, fromCryptoName, toCryptoName, balance);
            return ResponseEntity.ok("Criptomoeda convertida com sucesso!");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na conversão de criptomoeda: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @return Mensagem de retorno da correta execução das funções associadas à captura da criptomoeda sugerida ao usuário.
     */
    @GetMapping("/get-suggested-crypto")
    public ResponseEntity<?> suggestCryptoCurrency(@RequestParam String userId, @RequestParam String portfolioId) {
        try {
            return ResponseEntity.ok(portfolioService.suggestCryptoCurrency(userId, portfolioId));
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param strategyName Recebe o nome da estratégia de investimento a qual o usuário deseja implementar em seu portfolio.
     * @return Mensagem de retorno da correta execução das funções associadas à atribuição da estratégia de investmento.
     */
    @PostMapping("/set-Investment-Strategy")
    public ResponseEntity<String> setPortfolioInvestmentStrategy(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam StrategyNames strategyName) {
        try {
            portfolioService.setPortfolioInvestmentStrategy(userId, portfolioId, strategyName.getDisplayName());
            return ResponseEntity.ok("Estratégia de investimento atualizada com sucesso!");
        } catch (PortfolioNotFoundException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar a estratégia de investimento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param amount Recebe a quantia que o usuário deseja adicionar em seu investimento.
     * @return Mensagem de retorno da correta execução das funções associadas à adição de saldo no investimento do usuário.
     */
    @PostMapping("/add-balance")
    public ResponseEntity<String> addBalance(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam double amount) {
        try {
            portfolioService.addBalance(userId, portfolioId, amount);
            return ResponseEntity.ok("Saldo adicionado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor inválido para adicionar saldo: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param amount Recebe a quantia que o usuário deseja adicionar em seu investimento.
     * @return Mensagem de retorno da correta execução das funções associadas ao resgate do saldo desejado pelo usuário.
     */
    @PostMapping("/redeem-balance")
    public ResponseEntity<String> redeemBalance(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam double amount) {
        try {
            portfolioService.redeemBalance(userId, portfolioId, amount);
            return ResponseEntity.ok("Saldo resgatado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor inválido para resgatar saldo: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param cryptoName Recebe o nome da criptomoeda a qual o usuário deseja comprar.
     * @param amount Recebe a quantia que o usuário deseja adicionar em seu investimento.
     * @return Mensagem de retorno da correta execução das funções associadas à compra da criptomoeda de interesse do usuário.
     */
    @PostMapping("/buy-crypto")
    public ResponseEntity<String> buyCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.buyCrypto(userId, portfolioId, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda comprada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na compra de criptomoeda: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @param userId Recebe o ID do usuário associado.
     * @param portfolioId Recebe o ID do portfolio do usuário associado.
     * @param cryptoName Recebe o nome da criptomoeda a qual o usuário deseja vender.
     * @param amount Recebe a quantia que o usuário deseja retirar/vender do seu investimento.
     * @return Mensagem de retorno da correta execução das funções associadas so resgate do saldo desejado pelo usuário.
     */
    @PostMapping("/sell-crypto")
    public ResponseEntity<String> sellCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.sellCrypto(userId, portfolioId, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda vendida com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na venda de criptomoeda: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}