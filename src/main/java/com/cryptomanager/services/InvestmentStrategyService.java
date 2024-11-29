package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class InvestmentStrategyService {

    private static CryptoRepository cryptoRepository;

    @Autowired
    public InvestmentStrategyService(CryptoRepository cryptoRepository) {
        InvestmentStrategyService.cryptoRepository = cryptoRepository;
    }

    public static void updateInvestmentStrategyList(InvestmentStrategy investmentStrategy) throws IOException {
        investmentStrategy.getSuggestedCryptos().clear();
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

    public static InvestmentStrategy getInvestmentStrategyByName(String strategyName) {
        return switch (strategyName) { // Uso de enhaced switch
            case "CONSERVATIVE" -> new ConservativeStrategy();
            case "AGGRESSIVE" -> new AggressiveStrategy();
            case "MODERATE" -> new ModerateStrategy();
            default -> throw new IllegalArgumentException("Estratégia de investimento inválida");
        };
    }
}