package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.CurrencyConverter;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.cryptomanager.services.PortfolioService.findInvestmentIndex;

@Service
public class CurrencyConverterService {
    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;

    @Autowired
    public CurrencyConverterService(PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
    }

/*    public void convertCrypto(String portfolioId, String fromCryptoName, String toCryptoName, double balance) throws IOException {
        if (fromCryptoName == null || toCryptoName == null)
            throw new IllegalArgumentException("Parâmetros não podem ser nulos.");

        // Carregar todos os portfólios e encontrar o portfólio específico pelo ID
        List<Portfolio> allPortfolios = portfolioRepository.loadAllPortfolios();
        Portfolio targetPortfolio = null;

        for (Portfolio portfolio : allPortfolios) {
            if (portfolio.getId().equals(portfolioId)) {
                targetPortfolio = portfolio;
                break;
            }
        }

        if (targetPortfolio == null)
            throw new IllegalArgumentException("Portfólio não encontrado.");

        // Encontra o investimento na criptomoeda de origem e verificar se o saldo é suficiente
        Investment fromInvestment = findInvestment(targetPortfolio, fromCryptoName);
        if (fromInvestment.getCryptoInvestedQuantity() < balance) {
            throw new IllegalArgumentException("Saldo insuficiente ou investimento não encontrado.");
        }

        // Realiza a conversão de criptomoedas
        CurrencyConverter currencyConverter = new CurrencyConverter(cryptoRepository, findInvestmentListByCryptoName(toCryptoName));
        double convertedQuantity = currencyConverter.cryptoConverter(fromCryptoName, toCryptoName, balance);

        // Atualiza o portfólio: diminuir a quantidade de 'fromCrypto' e adicionar 'toCrypto'
        fromInvestment.setCryptoInvestedQuantity(fromInvestment.getCryptoInvestedQuantity() - balance);

        // Verifica se já existe investimento em 'toCrypto', caso contrário, criar um novo investimento
        Investment toInvestment = findInvestment(targetPortfolio, toCryptoName);
        toInvestment.setCryptoInvestedQuantity(toInvestment.getCryptoInvestedQuantity() + convertedQuantity);

        // Atualiza o arquivo com as novas informações
        portfolioRepository.appendConversionToFile(targetPortfolio, fromCryptoName, toCryptoName, balance, convertedQuantity);
    }
*/
    public void currencyConverter(String portfolioId, String userId, String fromCrypto, String toCrypto, double amount) throws IOException {
        CryptoCurrency crypto1 = cryptoRepository.loadCryptoByName(fromCrypto);
        CryptoCurrency crypto2 = cryptoRepository.loadCryptoByName(toCrypto);
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        int fromInvestmentIndex = findInvestmentIndex(portfolio, fromCrypto);
        int toInvestmentIndex = findInvestmentIndex(portfolio, toCrypto);
        double newAmount = crypto1.getPrice()*amount/crypto2.getPrice();

        //atualiza a crypto from
        portfolio.getInvestments().get(fromInvestmentIndex).setCryptoInvestedQuantity(portfolio.getInvestments().get(fromInvestmentIndex).getCryptoInvestedQuantity() - amount);

        Investment newInvestment;
        if(toInvestmentIndex == -1){ //Ou seja, o investimento "To" ainda nao existe na carteira
            newInvestment = new Investment(crypto2, crypto2.getPrice(), newAmount);
            portfolio.getInvestments().add(newInvestment);
        }

        else //atualiza a crypto to existente
            portfolio.getInvestments().get(toInvestmentIndex).setCryptoInvestedQuantity(portfolio.getInvestments().get(fromInvestmentIndex).getCryptoInvestedQuantity() + newAmount);

        portfolioRepository.updatePortfolio(portfolio);
    }
}
