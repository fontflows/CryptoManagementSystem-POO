package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cryptomanager.services.InvestmentStrategyService.updateCryptoList;

public class AggressiveStrategy implements InvestmentStrategy{
    private static final String name = "Aggressive";
    private static List<CryptoCurrency> aggressiveCryptos;
    private static final int riskQuota = 3;

    public AggressiveStrategy() throws IOException {
        aggressiveCryptos = new ArrayList<>();
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
        return aggressiveCryptos;
    }
}
