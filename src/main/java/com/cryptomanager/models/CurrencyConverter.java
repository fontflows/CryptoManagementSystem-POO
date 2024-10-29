package com.cryptomanager.models;

import java.util.List;

public class CurrencyConverter {
    private List<CryptoCurrency> cryptoList;

    public CurrencyConverter(List<CryptoCurrency> cryptoList) {
        this.cryptoList = cryptoList;
    }

    public List<CryptoCurrency> getCryptoList() {
        return cryptoList;
    }

    public void setCryptoList(List<CryptoCurrency> cryptoList) {
        this.cryptoList = cryptoList;
    }

    public double cryptoConverter(String fromCryptoName, String toCryptoName) {
        CryptoCurrency fromCrypto = findCryptoByName(fromCryptoName);
        CryptoCurrency toCrypto = findCryptoByName(toCryptoName);

        if (fromCrypto == null || toCrypto == null)
            throw new IllegalArgumentException("Criptomoeda não encontrada.");

        double taxConversionPrice = fromCrypto.getPrice()/toCrypto.getPrice();
        double taxConversionGrowthRate = fromCrypto.getGrowthRate()/toCrypto.getGrowthRate();

        return toCrypto.getMarketCap()*taxConversionGrowthRate*taxConversionPrice;
    }

    private CryptoCurrency findCryptoByName(String name) {
        for (CryptoCurrency crypto : cryptoList) {
            if (crypto.getName().equalsIgnoreCase(name))
                return crypto;
        }
        return null;
    }
}