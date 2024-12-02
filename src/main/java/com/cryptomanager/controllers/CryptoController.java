package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.services.CryptoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe responsavel por lidar com as criptomoedas a serem manipuladas no Swagger.
 */
@RestController
@RequestMapping("/cryptos")
public class CryptoController {
    private final CryptoService cryptoService;

    @Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /** Metodo responsavel por retornar todas as criptomoedas que estao cadastradas no sistema Swagger.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a captura de todas as criptomoedas inseridas no sistema.
     */
    @GetMapping
    public ResponseEntity<?> getAllCryptos() {
        try {
            return ResponseEntity.ok(cryptoService.getAllCryptos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por buscar uma criptomoeda presente no sistema, a partir do nome.
     * @param cryptoName Recebe o nome da criptomoeda a ser buscada.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a busca da criptomoeda, a partir do seu nome.
     */
    @GetMapping("/search-by-name")
    public ResponseEntity<?> getCryptoByName(String cryptoName) {
        try {
            return ResponseEntity.ok(cryptoService.getCryptoByName(cryptoName));
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel por adicionar dada criptomoeda de interesse ao sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @param price Recebe o preco.
     * @param growthRate Recebe a taxa de crescimento.
     * @param riskFactor Recebe o fator de risco.
     * @param availableAmount Recebe a quantidade da criptomoeda disponivel no mercado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a adicao da criptomoeda.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addCrypto(@RequestParam String cryptoName, @RequestParam double price, @RequestParam double growthRate, @RequestParam int riskFactor, @RequestParam double availableAmount) {
        try {
            cryptoService.addCrypto(cryptoName, price, growthRate, riskFactor, availableAmount);
            return ResponseEntity.ok("Criptomoeda adicionada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela delecao da criptomoeda de interesse do sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a delecao da criptomoeda.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCrypto(@RequestParam String cryptoName) {
        try {
            cryptoService.deleteCryptoByName(cryptoName);
            return ResponseEntity.ok("Criptomoeda removida com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** Metodo responsavel pela edicao de certa criptomoeda presente no sistema Swagger.
     * @param cryptoName Recebe o nome.
     * @param fieldToEdit Recebe o campo o qual se deseja editar na chamada do metodo.
     * @param newValue Recebe o novo valor a ser atrelado.
     * @return Mensagem de retorno da correta execucao das funcoes associadas a edicao da criptomoeda.
     */
    @PostMapping("/edit")
    public ResponseEntity<String> updateCrypto(@RequestParam String cryptoName, @Parameter(description = "Edit field",
            schema = @Schema(allowableValues = {"Price", "Growth Rate", "Risk Factor"})) @RequestParam String fieldToEdit,
                                               @RequestParam String newValue) {

        try{
            cryptoService.updateCrypto(cryptoName, fieldToEdit, newValue);
            return ResponseEntity.ok("Criptomoeda atualizada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
