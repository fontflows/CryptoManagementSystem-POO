package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.services.CryptoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/cryptos")
public class CryptoController {

    private final CryptoService cryptoService;

    @Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCryptos() {
        try {
            return ResponseEntity.ok(cryptoService.getAllCryptos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search-by-name")
    public ResponseEntity<?> getCryptoByName(String cryptoName) {
        try {
            return ResponseEntity.ok(cryptoService.getCryptoByName(cryptoName));
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/add-ADMIN")
    public ResponseEntity<String> addCrypto(@RequestParam String cryptoName, @RequestParam double price, @RequestParam double growthRate, @RequestParam int riskFactor, @RequestParam double availableAmount) {
        try {
            cryptoService.addCrypto(cryptoName, price, growthRate, riskFactor, availableAmount);
            return ResponseEntity.ok("Criptomoeda adicionada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-ADMIN")
    public ResponseEntity<String> deleteCrypto(@RequestParam String cryptoName) {
        try {
            cryptoService.deleteCryptoByName(cryptoName);
            return ResponseEntity.ok("Criptomoeda removida com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/edit-ADMIN")
    public ResponseEntity<String> updateCrypto(@RequestParam String cryptoName, @Parameter(description = "Edit field", schema = @Schema(allowableValues = {"Price", "Growth Rate", "Risk Factor"})) @RequestParam String fieldToEdit, @RequestParam String newValue) {
        try{
            cryptoService.updateCrypto(cryptoName, fieldToEdit, newValue);
            return ResponseEntity.ok("Criptomoeda atualizada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
