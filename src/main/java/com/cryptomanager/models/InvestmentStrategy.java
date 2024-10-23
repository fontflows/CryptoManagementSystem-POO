package com.cryptomanager.models;

import java.util.List;

public interface InvestmentStrategy {
    void setCryptos(List<CryptoCurrency> investments);
    void addCryptoCurrency(CryptoCurrency crypto);
    void removeCryptoCurrency(CryptoCurrency crypto);
    List<CryptoCurrency> getCryptos();
    CryptoCurrency getRandomCrypto();
}
