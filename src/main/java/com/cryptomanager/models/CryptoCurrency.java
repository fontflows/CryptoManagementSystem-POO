package com.cryptomanager.models;

public class CryptoCurrency {
    private final String name;
    private final double price;

    public CryptoCurrency(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "CryptoCurrency{name='" + name + "', price=" + price + "}";
    }
}