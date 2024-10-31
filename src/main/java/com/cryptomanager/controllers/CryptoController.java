package com.cryptomanager.controllers;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.services.CryptoService;
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
    public List<CryptoCurrency> getAllCryptos() {
        return cryptoService.getAllCryptos();
    }

    @PostMapping("/add")
    public String addCrypto(@RequestBody CryptoCurrency crypto) {
        try {
            cryptoService.addCrypto(crypto);
            return "Criptomoeda adicionada com sucesso!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteCrypto(@RequestParam String name) {
        try {
            cryptoService.deleteCryptoByName(name);
            return "Criptomoeda removida com sucesso!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
