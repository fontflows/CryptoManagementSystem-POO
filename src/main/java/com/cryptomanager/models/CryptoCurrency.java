package com.cryptomanager.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo que representa uma criptomoeda")
public class CryptoCurrency {
    @Schema(description = "Nome da criptomoeda", example = "Bitcoin")
    private final String name;

    @Schema(description = "Preço atual da criptomoeda", example = "50000.0")
    private double price;

    @Schema(description = "Taxa de crescimento da criptomoeda", example = "0.05")
    private double growthRate;

    @Schema(description = "Capitalização de mercado da criptomoeda", example = "900000000000.0")
    private double marketCap;

    @Schema(description = "Volume negociado nas últimas 24 horas", example = "20000000.0")
    private double volume24h;

    @Schema(description = "Fator de risco da criptomoeda (1-3)", example = "3")
    private int riskFactor;

    public CryptoCurrency(String name, double price,double growthRate, double marketCap, double volume24h, int riskFactor) {
        if (price <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if (growthRate <= -1) {
            throw new IllegalArgumentException("A taxa de crescimento deve ser maior que -1");
        }
        if (marketCap <= 0) {
            throw new IllegalArgumentException("O valor de market cap deve ser maior que zero.");
        }
        if (volume24h < 0) {
            throw new IllegalArgumentException("O volume em 24h não pode ser negativo.");
        }
        if (riskFactor < 1 || riskFactor > 3) {
            throw new IllegalArgumentException("O fator de risco deve estar entre 1 e 3.");
        }

        this.name = name;
        this.price = price;
        this.growthRate = growthRate;
        this.marketCap = marketCap;
        this.volume24h = volume24h;
        this.riskFactor = riskFactor;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMarketCap() { return marketCap;}

    public void setMarketCap(double marketCap){ this.marketCap = marketCap;}

    public double getGrowthRate() { return growthRate;}

    public void setGrowthRate(double growthRate) {this.growthRate = growthRate;}

    public double getVolume24h() {return volume24h;}

    public void setVolume24h(double volume24h) {this.volume24h = volume24h;}

    public int getRiskFactor() {return riskFactor;}

    public void setRiskFactor(int riskFactor) {this.riskFactor = riskFactor;}

    @Override
    public String toString() {
        return name + "," + price + "," + growthRate + "," + marketCap + "," + volume24h + "," + riskFactor;
    }
}
