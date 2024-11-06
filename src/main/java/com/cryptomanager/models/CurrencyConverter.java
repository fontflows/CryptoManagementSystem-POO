package com.cryptomanager.models;

import com.cryptomanager.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CurrencyConverter {
    private List<CryptoCurrency> cryptoList;
    private List<Investment> investmentList;

    @Autowired
    public CurrencyConverter(CryptoRepository cryptoRepository, List<Investment> investmentList) throws IOException {
        this.cryptoList = cryptoRepository.loadCryptos();
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

    public double cryptoConverter(String fromCryptoName, String toCryptoName, double balance) {
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
            throw new IllegalArgumentException("Saldo excede o total investido, previamente informado.");
    }

    public CryptoCurrency findCryptoByName(String cryptoName) {
        for (CryptoCurrency crypto : cryptoList) {
            if (crypto.getName().equalsIgnoreCase(cryptoName))
                return crypto;
        }
        return null;
    }

    public Investment findInvestmentByName(String cryptoName) {
        for (Investment investment : investmentList) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName))
                return investment;
        }
        return null;
    }
}