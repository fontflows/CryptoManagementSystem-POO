package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;

public class InvestmentStrategy {
    private String name;
    private List<CryptoCurrency> suggestedCryptos = new ArrayList<>();
    private int riskQuota;

    public InvestmentStrategy(String name, int riskQuota){
        this.name = name;
        this.riskQuota = riskQuota;
    }

    public void setInvestmentStrategyName(String name){
        this.name = name;
    }

    public String getInvestmentStrategyName(){
        return name;
    }

    public void setRiskQuota(int riskQuota){
        this.riskQuota = riskQuota;
    }

    public int getRiskQuota(){
            return riskQuota;
    }

    public void setSuggestedCryptos(List<CryptoCurrency> suggestedCryptos){
        this.suggestedCryptos = suggestedCryptos;
    }

    public List<CryptoCurrency> getSuggestedCryptos(){
        return suggestedCryptos;
    }
}
