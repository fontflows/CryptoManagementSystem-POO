package com.cryptomanager.models;

public class Investment {
    private double purchasePrice; // preço de compra da crypto
    private CryptoCurrency cryptoCurrency;
    private double cryptoInvestedQuantity;

    public Investment(CryptoCurrency cryptoCurrency, double purchasePrice, double cryptoInvestedQuantity) {
        this.cryptoCurrency = cryptoCurrency;
        this.purchasePrice = purchasePrice;
        this.cryptoInvestedQuantity = cryptoInvestedQuantity;
    }

    public double getCryptoInvestedQuantity() {
        return cryptoInvestedQuantity;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public void setCryptoInvestedQuantity(double cryptoInvestedQuantity) {
        this.cryptoInvestedQuantity = cryptoInvestedQuantity;
    }

    @Override
    public String toString() {
        return cryptoCurrency.getName() + ", " + cryptoInvestedQuantity + ", " + purchasePrice; /* Formato: nomeCrypto,
        quantidadeInvestida, precoCompra*/
    }
}