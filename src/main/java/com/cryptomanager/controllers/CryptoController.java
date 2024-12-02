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
@RequestMapping("2/cryptos")
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

}
