package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.cryptomanager.services.PortfolioService.findInvestment;
import static com.cryptomanager.services.PortfolioService.hasCrypto;

@Service
public class CurrencyConverterService {
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;

    @Autowired
    public CurrencyConverterService(PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
    }

    public void currencyConverter(String portfolioId, String userId, String fromCrypto, String toCrypto, double cryptoAmount) throws IOException {
        if(cryptoAmount <= 0)
            throw new IllegalArgumentException("Quantidade de criptomoedas a serem convertidas deve ser maior que zero");

        CryptoCurrency cryptoFrom = cryptoRepository.loadCryptoByName(fromCrypto);
        CryptoCurrency cryptoTo = cryptoRepository.loadCryptoByName(toCrypto);
        if(cryptoFrom == null || cryptoTo == null)
            throw new IllegalArgumentException("Criptomoedas não encontradas");

        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
        if(!hasCrypto(fromCrypto, portfolio))
            throw new IllegalArgumentException("Criptomoeda " + fromCrypto + " não encontrada no portfólio");

        if(portfolio.getAssetAmount(fromCrypto) < cryptoAmount)
            throw new IllegalArgumentException("Quantidade da criptomoeda " + fromCrypto + " insuficiente no portfólio");

        Investment fromInvestment = findInvestment(portfolio, cryptoFrom.getName());
        double newAmount = (cryptoFrom.getPrice()*cryptoAmount)/cryptoTo.getPrice();
        double fromOldAmount = fromInvestment.getCryptoInvestedQuantity();

        //Atualiza o investimento "From"
        if(fromOldAmount - cryptoAmount == 0)
            portfolio.getInvestments().remove(fromInvestment);
        else
            fromInvestment.setCryptoInvestedQuantity(fromOldAmount - cryptoAmount);

        //Se a crypto "To" não existe no portfolio
        if(!hasCrypto(toCrypto, portfolio)){
            Investment newInvestment = new Investment(cryptoTo, cryptoTo.getPrice(), newAmount);
            portfolio.getInvestments().add(newInvestment);
        }
        //atualiza a crypto "To" existente
        else {
            Investment toInvestment = findInvestment(portfolio, cryptoTo.getName());
            double toOldAmount = toInvestment.getCryptoInvestedQuantity();
            double avaragePrice = (toInvestment.getPurchasePrice()*toOldAmount + cryptoTo.getPrice()*newAmount)/(toOldAmount+newAmount);
            toInvestment.setCryptoInvestedQuantity(avaragePrice);
            toInvestment.setCryptoInvestedQuantity(toOldAmount + newAmount);
        }
        portfolioRepository.updatePortfolio(portfolio);
    }
}
