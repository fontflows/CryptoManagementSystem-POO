package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModerateStrategy implements InvestmentStrategy{
    private static final String strategyName = "Moderate";
    private final List<CryptoCurrency> moderateCryptos;
    private static final int riskQuota = 2;

    public ModerateStrategy() throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        moderateCryptos = new ArrayList<>();
        for(CryptoCurrency crypto : cryptos){
            if(crypto.getRiskFactor() == riskQuota) {
                moderateCryptos.add(crypto);
            }
        }
    }

    @Override
    public String getInvestmentStrategyName() {
        return strategyName;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return moderateCryptos;
    }

    @Override
    public CryptoCurrency getRandomCrypto() {
        Random rng = new Random();
        if (moderateCryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return moderateCryptos.get(rng.nextInt(moderateCryptos.size()));
    }
}
