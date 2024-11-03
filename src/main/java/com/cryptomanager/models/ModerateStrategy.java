package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cryptomanager.services.InvestmentStrategyService.updateCryptoList;

public class ModerateStrategy implements InvestmentStrategy{
    private static final String strategyName = "Moderate";
    private static List<CryptoCurrency> moderateCryptos;
    private static final int riskQuota = 2;

    public ModerateStrategy() throws IOException {
        moderateCryptos = new ArrayList<>();
    }

    @Override
    public String getInvestmentStrategyName() {
        return strategyName;
    }

    @Override
    public int getRiskQuota() {
        return riskQuota;
    }

    @Override
    public List<CryptoCurrency> getCryptos() {
        return moderateCryptos;
    }
}
