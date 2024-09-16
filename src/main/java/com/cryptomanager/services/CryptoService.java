package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoService {
    private final List<CryptoCurrency> cryptoList = new ArrayList<>();

    // Construtor padrão
    public CryptoService() {
    }

    public List<CryptoCurrency> getAllCryptos() {
        return cryptoList;
    }

    public void addCrypto(CryptoCurrency crypto) {
        cryptoList.add(crypto);
    }
}
