package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class InvestmentStrategyService {

    public static void updateCryptoList(InvestmentStrategy investmentStrategy) throws IOException {
        investmentStrategy.getCryptos().clear();
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        for(CryptoCurrency crypto : cryptos){
            if(crypto.getRiskFactor() <= investmentStrategy.getRiskQuota()) {
                investmentStrategy.getCryptos().add(crypto);
            }
        }
    }

    public static void updateAllStrategiesList() throws IOException {
        updateCryptoList(new ConservativeStrategy());
        updateCryptoList(new AggressiveStrategy());
        updateCryptoList(new ModerateStrategy());
    }

    public static CryptoCurrency getRandomCrypto(InvestmentStrategy investmentStrategy) {
        List<CryptoCurrency> cryptos = investmentStrategy.getCryptos();
        Random rng = new Random();
        if (cryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return cryptos.get(rng.nextInt(cryptos.size()));
    }

    public static InvestmentStrategy getInvestmentStrategyByName(String strategyName) throws IOException {
        if(strategyName.equals("Conservative")){
            return new ConservativeStrategy();
        }
        else if(strategyName.equals("Aggressive")){
            return new AggressiveStrategy();
        }
        else if(strategyName.equals("Moderate")){
            return new ModerateStrategy();
        }
        else{
            throw new IllegalArgumentException("Estratégia de investimento inválida");
        }
    }
}
