package com.cryptomanager.controllers;

import com.cryptomanager.exceptions.CryptoServiceException;
import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.services.CryptoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @PostMapping("/add")
    public ResponseEntity<String> addCrypto(@RequestParam String cryptoName, @RequestParam double price, @RequestParam double growthRate, @RequestParam double marketCap, @RequestParam double volume24h, @RequestParam int riskFactor) {
        try {
            cryptoService.addCrypto(cryptoName, price, growthRate, marketCap, volume24h, riskFactor);
            return ResponseEntity.ok("Criptomoeda adicionada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCrypto(@RequestParam String cryptoName) {
        try {
            cryptoService.deleteCryptoByName(cryptoName);
            return ResponseEntity.ok("Criptomoeda removida com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<String> updateCrypto(@RequestBody CryptoCurrency crypto) {
        try {
            cryptoService.updateCrypto(crypto);
            return ResponseEntity.ok("Criptomoeda atualizada com sucesso!");
        } catch (CryptoServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
