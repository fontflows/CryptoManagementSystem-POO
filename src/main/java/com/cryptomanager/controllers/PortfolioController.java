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
 * Classe responsavel por lidar com a manipulacao das funcionalidades do portfolio de investimento dos usuarios.
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

    /** Metodo responsavel por calcular o valor total acumulado nos portfolios gerados durante a interacao do usuario com o sistema Swagger.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a captura do valor total.
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

    /** Metodo responsavel por realizar a conversao de um tipo de criptomoeda para outro, a partir do ID do portfolio informado pelo usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param fromCryptoName Recebe o nome da criptomoeda a ser convertida.
     * @param toCryptoName Recebe o nome da criptomoeda de interesse do usuario a ser obtida.
     * @param balance Recebe o saldo que o usuario deseja converter.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a conversao do saldo.
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

    /** Metodo responsavel por ofertar ao usuario a criptomoeda sugerida para realizar investimentos, considerando o seu portfolio.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a captura da criptomoeda sugerida ao usuario.
     */
    @GetMapping("/get-suggested-crypto")
    public ResponseEntity<?> suggestCryptoCurrency(@RequestParam String userId, @RequestParam String portfolioId) {
        try {
            return ResponseEntity.ok(portfolioService.suggestCryptoCurrency(userId, portfolioId));
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /** Metodo responsavel por atribuir o tipo de estrategia de investimento ao portfolio criado, durante a interacao do usuario com o Swagger.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param strategyName Recebe o nome da estrategia de investimento a qual o usuario deseja implementar em seu portfolio.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a atribuicao da estrategia de investimento.
     */
    @PostMapping("/set-Investment-Strategy")
    public ResponseEntity<String> setPortfolioInvestmentStrategy(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam StrategyNames strategyName) {
        try {
            portfolioService.setPortfolioInvestmentStrategy(userId, portfolioId, strategyName.getDisplayName());
            return ResponseEntity.ok("Estratégia de investimento atualizada com sucesso!");
        } catch (PortfolioNotFoundException | IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao atualizar a estrategia de investimento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /** Metodo responsavel por adicionar o saldo de interesse do usuario para o seu portfolio.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param amount Recebe a quantia que o usuario deseja adicionar em seu investimento.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a adicao de saldo no investimento do usuario.
     */
    @PostMapping("/add-balance")
    public ResponseEntity<String> addBalance(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam double amount) {
        try {
            portfolioService.addBalance(userId, portfolioId, amount);
            return ResponseEntity.ok("Saldo adicionado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor invalido para adicionar saldo: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /** Metodo responsavel por realizar o resgate de saldo do portfolio do usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param amount Recebe a quantia que o usuario deseja resgatar de seu investimento.
     * @return Mensagem de retorno da correta execucao das funcoes associadas ao resgate do saldo desejado pelo usuario.
     */
    @PostMapping("/redeem-balance")
    public ResponseEntity<String> redeemBalance(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam double amount) {
        try {
            portfolioService.redeemBalance(userId, portfolioId, amount);
            return ResponseEntity.ok("Saldo resgatado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor invalido para resgatar saldo: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /** Metodo responsavel por realizar a compra da criptomoeda de interesse do usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param cryptoName Recebe o nome da criptomoeda a qual o usuario deseja comprar.
     * @param amount Recebe a quantia que o usuario deseja comprar em seu investimento.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a compra da criptomoeda de interesse do usuario.
     */
    @PostMapping("/buy-crypto")
    public ResponseEntity<String> buyCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.buyCrypto(userId, portfolioId, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda comprada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na compra de criptomoeda: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /** Metodo responsavel por operar a venda da criptomoeda de interesse do usuario.
     * @param userId Recebe o ID do usuario associado.
     * @param portfolioId Recebe o ID do portfolio do usuario associado.
     * @param cryptoName Recebe o nome da criptomoeda a qual o usuario deseja vender.
     * @param amount Recebe a quantia que o usuario deseja retirar/vender do seu investimento.
     * @return Mensagem de retorno da correta execucao das funcoes associadas so resgate do saldo desejado pelo usuario.
     */
    @PostMapping("/sell-crypto")
    public ResponseEntity<String> sellCrypto(@RequestParam String userId, @RequestParam String portfolioId, @RequestParam String cryptoName, @RequestParam double amount) {
        try {
            portfolioService.sellCrypto(userId, portfolioId, cryptoName, amount);
            return ResponseEntity.ok("Criptomoeda vendida com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro na venda de criptomoeda: " + e.getMessage());
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portfólio nao encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}