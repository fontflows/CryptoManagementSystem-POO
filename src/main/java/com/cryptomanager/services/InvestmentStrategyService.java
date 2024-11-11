package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class InvestmentStrategyService {

    public static void updateInvestmentStrategyList(InvestmentStrategy investmentStrategy) throws IOException {
        investmentStrategy.getSuggestedCryptos().clear();
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        for(CryptoCurrency crypto : cryptos){
            if(crypto.getRiskFactor() <= investmentStrategy.getRiskQuota()) {
                investmentStrategy.getSuggestedCryptos().add(crypto);
            }
        }
    }

    public static CryptoCurrency getRandomCrypto(InvestmentStrategy investmentStrategy) {
        List<CryptoCurrency> cryptos = investmentStrategy.getSuggestedCryptos();
        Random rng = new Random();
        if (cryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return cryptos.get(rng.nextInt(cryptos.size()));
    }

    public static InvestmentStrategy getInvestmentStrategyByName(String strategyName) throws IOException {
         switch (strategyName) {
            case "Conservative": return new ConservativeStrategy();
            case "Aggressive": return new AggressiveStrategy();
            case "Moderate": return new ModerateStrategy();
            default: throw new IllegalArgumentException("Estratégia de investimento inválida");
        }
    }
}
