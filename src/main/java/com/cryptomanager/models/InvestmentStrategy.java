package com.cryptomanager.models;

import java.util.List;

public interface InvestmentStrategy {
    String getInvestmentStrategyName();
    List<CryptoCurrency> getCryptos();
    CryptoCurrency getRandomCrypto();
}
