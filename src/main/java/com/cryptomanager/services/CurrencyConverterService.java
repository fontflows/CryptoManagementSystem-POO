package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.cryptomanager.services.PortfolioService.findInvestmentIndex;
import static com.cryptomanager.services.PortfolioService.hasAsset;

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

        CryptoCurrency crypto1 = cryptoRepository.loadCryptoByName(fromCrypto);
        CryptoCurrency crypto2 = cryptoRepository.loadCryptoByName(toCrypto);
        if(crypto1 == null || crypto2 == null)
            throw new IllegalArgumentException("Criptomoedas não encontradas");

        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
        if(!hasAsset(fromCrypto, portfolio))
            throw new IllegalArgumentException("Criptomoeda " + fromCrypto + " não encontrada no portfólio");

        if(portfolio.getAssetAmount(fromCrypto) < cryptoAmount)
            throw new IllegalArgumentException("Quantidade da criptomoeda " + fromCrypto + " insuficiente no portfólio");

        int fromInvestmentIndex = findInvestmentIndex(portfolio, fromCrypto);
        int toInvestmentIndex = findInvestmentIndex(portfolio, toCrypto);
        double newAmount = crypto1.getPrice()*cryptoAmount/crypto2.getPrice();

        //Atualiza a crypto "From"
        if(portfolio.getInvestments().get(fromInvestmentIndex).getCryptoInvestedQuantity() - cryptoAmount == 0)
            portfolio.getInvestments().remove(fromInvestmentIndex);
        else
            portfolio.getInvestments().get(fromInvestmentIndex).setCryptoInvestedQuantity(portfolio.getInvestments().get(fromInvestmentIndex).getCryptoInvestedQuantity() - cryptoAmount);

        //Se a crypto "To" não existe no portfolio
        if(toInvestmentIndex == -1){
            Investment newInvestment = new Investment(crypto2, crypto2.getPrice(), newAmount);
            portfolio.getInvestments().add(newInvestment);
        }
        //atualiza a crypto "To" existente
        else {
            double oldQuantity = portfolio.getInvestments().get(toInvestmentIndex).getCryptoInvestedQuantity();
            double avaragePrice = (portfolio.getInvestments().get(toInvestmentIndex).getPurchasePrice()*oldQuantity + crypto2.getPrice()*newAmount)/(oldQuantity+newAmount);
            portfolio.getInvestments().get(toInvestmentIndex).setPurchasePrice(avaragePrice);
            portfolio.getInvestments().get(toInvestmentIndex).setCryptoInvestedQuantity(portfolio.getInvestments().get(toInvestmentIndex).getCryptoInvestedQuantity() + newAmount);
        }
        portfolioRepository.updatePortfolio(portfolio);
    }
}
