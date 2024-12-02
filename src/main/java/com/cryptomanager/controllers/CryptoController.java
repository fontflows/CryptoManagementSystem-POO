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
@RequestMapping("2/cryptos")
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
}
