package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CryptoService {

    private final CryptoRepository cryptoRepository;

    @Autowired
    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    public List<CryptoCurrency> getAllCryptos() {
        try {
            return cryptoRepository.loadCryptos();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Retorna uma lista vazia em caso de erro
        }
    }

    public void addCrypto(CryptoCurrency crypto) {
        try {
            cryptoRepository.saveCrypto(crypto);
        } catch (IOException e) {
            e.printStackTrace();
            // Você pode querer lançar uma exceção personalizada aqui
        }
    }
}