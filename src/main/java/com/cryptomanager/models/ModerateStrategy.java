package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModerateStrategy implements InvestmentStrategy{
    private static final String name = "Moderate";
    private final List<String> moderateCryptos;
    private static final int riskQuota = 2;

    public ModerateStrategy() throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        moderateCryptos = new ArrayList<>();
        for(CryptoCurrency c : cryptos){
            if(c.getRiskFactor() == riskQuota) {
                moderateCryptos.add(c.getName());
            }
        }
    }

    @Override
    public String getInvestmentStrategyName() {
        return name;
    }

    @Override
    public List<String> getCryptos() {
        return moderateCryptos;
    }

    @Override
    public String getRandomCrypto() {
        Random rng = new Random();
        if (moderateCryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return moderateCryptos.get(rng.nextInt(moderateCryptos.size()));
    }
}
