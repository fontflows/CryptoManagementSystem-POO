package com.cryptomanager.models;

import java.util.List;

public class CurrencyConverter {
    private List<CryptoCurrency> cryptoList;
    private List<Investment> investmentList;

    public CurrencyConverter(List<CryptoCurrency> cryptoList, List<Investment> investmentList) {
        this.cryptoList = cryptoList;
        this.investmentList = investmentList;
    }

    public List<CryptoCurrency> getCryptoList() {
        return cryptoList;
    }

    public void setCryptoList(List<CryptoCurrency> cryptoList) {
        this.cryptoList = cryptoList;
    }

    public List<Investment> getPortfolioList() {
        return investmentList;
    }

    public void setPortfolioList(List<Investment> investmentList) {
        this.investmentList = investmentList;
    }

    public double cryptoConverter(String fromCryptoName, String toCryptoName) {
        CryptoCurrency fromCrypto = findCryptoByName(fromCryptoName);
        Investment investmentListFromCrypto = findInvestmentByName(fromCryptoName);
        CryptoCurrency toCrypto = findCryptoByName(toCryptoName);

        if (fromCrypto == null || toCrypto == null || investmentListFromCrypto == null)
            throw new IllegalArgumentException("Criptomoeda não encontrada.");

        double taxConversionPrice = fromCrypto.getPrice()/toCrypto.getPrice();
        double quantityOriginalCryptoQuantityInvested = investmentListFromCrypto.getCryptoInvestedQuantity();

        return quantityOriginalCryptoQuantityInvested*taxConversionPrice;
    }

    private CryptoCurrency findCryptoByName(String name) {
        for (CryptoCurrency crypto : cryptoList) {
            if (crypto.getName().equalsIgnoreCase(name))
                return crypto;
        }
        return null;
    }

    private Investment findInvestmentByName(String name) {
        for (Investment investment : investmentList) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(name))
                return investment;
        }
        return null;
    }
}
