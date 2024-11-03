package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cryptomanager.services.InvestmentStrategyService.updateCryptoList;

public class ConservativeStrategy implements InvestmentStrategy{
    private static final String name = "Conservative";
    private static List<CryptoCurrency> conservativeCryptos;
    private static final int riskQuota = 1;

    public ConservativeStrategy() throws IOException {
        conservativeCryptos = new ArrayList<>();
    }

    @Override
    public String getInvestmentStrategyName() {
        return name;
    }

    @Override
    public int getRiskQuota() {
        return riskQuota;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return conservativeCryptos;
    }
}
