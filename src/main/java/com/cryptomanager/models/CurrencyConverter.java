package com.cryptomanager.models;

import java.util.List;

public class CurrencyConverter {
    private static List<CryptoCurrency> cryptoList;
    private static List<Investment> investmentList;

    public CurrencyConverter(List<CryptoCurrency> cryptoList, List<Investment> investmentList) {
        CurrencyConverter.cryptoList = cryptoList;
        CurrencyConverter.investmentList = investmentList;
    }

    public List<CryptoCurrency> getCryptoList() {
        return cryptoList;
    }

    public void setCryptoList(List<CryptoCurrency> cryptoList) {
        CurrencyConverter.cryptoList = cryptoList;
    }

    public List<Investment> getInvestmentList() {
        return investmentList;
    }

    public void setInvestmentList(List<Investment> investmentList) {
        CurrencyConverter.investmentList = investmentList;
    }

    public static double cryptoConverter(String fromCryptoName, String toCryptoName, double balance) {
        CryptoCurrency fromCrypto = findCryptoByName(fromCryptoName);
        Investment investmentListFromCrypto = findInvestmentByName(fromCryptoName);
        CryptoCurrency toCrypto = findCryptoByName(toCryptoName);

        if (fromCrypto == null || toCrypto == null || investmentListFromCrypto == null)
            throw new IllegalArgumentException("Criptomoeda não encontrada.");

        double taxConversionPrice = fromCrypto.getPrice()/toCrypto.getPrice();
        double quantityOriginalCryptoQuantityInvested = investmentListFromCrypto.getCryptoInvestedQuantity();

        if (balance <= 0)
            throw new IllegalArgumentException("Saldo deve ser positivo.");

        else if (balance <= quantityOriginalCryptoQuantityInvested)
            return balance*taxConversionPrice;

        else
            throw new IllegalArgumentException("Saldo excede o total investido, previamente, informado.");

    }

    public static CryptoCurrency findCryptoByName(String cryptoName) {
        for (CryptoCurrency crypto : cryptoList) {
            if (crypto.getName().equals(cryptoName))
                return crypto;
        }
        return null;
    }

    public static Investment findInvestmentByName(String cryptoName) {
        for (Investment investment : investmentList) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName))
                return investment;
        }
        return null;
    }
}