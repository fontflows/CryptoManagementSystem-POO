package com.cryptomanager.models;

import java.util.List;

public interface InvestmentStrategy {
    String getInvestmentStrategyName();
    int getRiskQuota();
    List<CryptoCurrency> getCryptos();
}
