package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AggressiveStrategy implements InvestmentStrategy{
    private static final String name = "Aggressive";
    private final List<CryptoCurrency> aggressiveCryptos;
    private static final int riskQuota = 3;

    public AggressiveStrategy() throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        aggressiveCryptos = new ArrayList<>();
        for(CryptoCurrency crypto : cryptos){
            if(crypto.getRiskFactor() == riskQuota) {
                aggressiveCryptos.add(crypto);
            }
        }
    }

    @Override
    public String getInvestmentStrategyName() {
        return name;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return aggressiveCryptos;
    }

    @Override
    public CryptoCurrency getRandomCrypto() {
        Random rng = new Random();
        if (aggressiveCryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return aggressiveCryptos.get(rng.nextInt(aggressiveCryptos.size()));
    }
}
