package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConservativeStrategy implements InvestmentStrategy{
    private static final String name = "Conservative";
    private  final List<String> conservativeCryptos;
    private static final int riskQuota = 1;

    public ConservativeStrategy() throws IOException {
        CryptoRepository cryptoRepository = new CryptoRepository();
        List<CryptoCurrency> cryptos = cryptoRepository.loadCryptos();
        conservativeCryptos = new ArrayList<>();
        for(CryptoCurrency c : cryptos){
            if(c.getRiskFactor() == riskQuota) {
                conservativeCryptos.add(c.getName());
            }
        }
    }

    @Override
    public String getInvestmentStrategyName() {
        return name;
    }

    @Override
    public List<String> getCryptos() {
        return conservativeCryptos;
    }

    @Override
    public String getRandomCrypto() {
        Random rng = new Random();
        if (conservativeCryptos.isEmpty())
            throw new IllegalStateException("A lista de criptomoedas está vazia.");
        return conservativeCryptos.get(rng.nextInt(conservativeCryptos.size()));
    }
}
