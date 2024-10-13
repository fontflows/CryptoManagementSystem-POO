package com.cryptomanager.models;

public class CryptoCurrency {
    private String name;
    private double price;
    private double growthRate;
    private double marketCap;
    private double volume24h;

    public CryptoCurrency(String name, double price,double growthRate, double marketCap, double volume24h) {
        this.name = name;
        this.price = price;
		this.growthRate = growthRate;
		this.marketCap = marketCap;
		this.volume24h = volume24h;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return name + "," + price + "," + growthRate + "," + marketCap + "," + volume24h;
    }
}