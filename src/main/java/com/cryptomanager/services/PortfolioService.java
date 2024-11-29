package com.cryptomanager.services;

import com.cryptomanager.exceptions.InvestmentStrategyNotFoundException;
import com.cryptomanager.exceptions.NoCryptosSuggestedException;
import com.cryptomanager.exceptions.PortfolioNotFoundException;
import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.cryptomanager.repositories.CryptoRepository.loadCryptoByName;
import static com.cryptomanager.repositories.TransactionsRepository.saveBuyTransaction;
import static com.cryptomanager.repositories.TransactionsRepository.saveSellTransaction;
import static com.cryptomanager.services.InvestmentStrategyService.getInvestmentStrategyByName;
import static com.cryptomanager.services.InvestmentStrategyService.getRandomCrypto;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CryptoRepository cryptoRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, CryptoRepository cryptoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.cryptoRepository = cryptoRepository;
    }

    // Calcula o valor total do portfólio com base no preço atual das criptomoedas
    public double calculateTotalValue(String userId, String portfolioId) {
        double totalValue = 0.0;
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado para o usuário " + userId);

        for (Investment investment : portfolio.getInvestments()) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice();
            double quantity = investment.getCryptoInvestedQuantity();
            totalValue += actualPrice * quantity;
        }

        return totalValue;
    }

    // Retorna o investimento pelo nome da criptomoeda
    public static Investment findInvestment(Portfolio portfolio, String cryptoName) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return investment;
        }
        throw new IllegalArgumentException("Investimento não encontrado para a criptomoeda " + cryptoName);
    }

    // Verifica se o portfólio contém uma criptomoeda
    public static boolean hasCrypto(String cryptoName, Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return true;
        }
        return false;
    }

    // Sugere uma criptomoeda com base na estratégia de investimento
    public CryptoCurrency suggestCryptoCurrency(String userID, String portfolioID) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();
        if (investmentStrategy == null)
            throw new InvestmentStrategyNotFoundException("Estratégia de investimento não configurada para o portfólio.");

        InvestmentStrategyService.updateInvestmentStrategyList(investmentStrategy);

        if (investmentStrategy.getSuggestedCryptos().isEmpty())
            throw new NoCryptosSuggestedException("Nenhuma criptomoeda disponível para sugestão na estratégia " + investmentStrategy.getInvestmentStrategyName());

        return getRandomCrypto(investmentStrategy);
    }

    // Configura a estratégia de investimento do portfólio
    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        if (strategyName.equals(portfolio.getInvestmentStrategy().getInvestmentStrategyName())) return; // Caso selecione a mesma estratégia, não é necessário alterar nada

        InvestmentStrategy strategy = getInvestmentStrategyByName(strategyName);

        portfolio.setInvestmentStrategy(strategy);
        portfolioRepository.updatePortfolio(portfolio);
    }

    // Adiciona saldo ao portfólio
    public void addBalance(String userID, String portfolioID, double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior que zero");

        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        portfolio.setBalance(portfolio.getBalance() + amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    // Realiza o resgate de saldo do portfólio
    public void redeemBalance(String userID, String portfolioID, double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgatar deve ser maior que zero");

        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        if (amount > portfolio.getBalance())
            throw new IllegalArgumentException("Valor inserido para resgate é maior que o saldo disponível");

        portfolio.setBalance(portfolio.getBalance() - amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    // Compra uma criptomoeda para o portfólio
    public void buyCrypto(String userID, String portfolioID, String cryptoName, double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("Quantidade para compra deve ser maior que zero");

        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        CryptoCurrency crypto = loadCryptoByName(cryptoName);

        double totalCost = amount * crypto.getPrice();
        if (portfolio.getBalance() < totalCost)
            throw new IllegalArgumentException("Saldo disponível não é suficiente para essa compra");

        portfolio.setBalance(portfolio.getBalance() - totalCost);

        if (hasCrypto(cryptoName, portfolio)) {
            Investment updatedInvestment = findInvestment(portfolio, cryptoName);
            // Calcula o preço médio ponderado da compra
            double newPurchasePrice = (crypto.getPrice() * amount + updatedInvestment.getCryptoInvestedQuantity() * updatedInvestment.getPurchasePrice())
                    / (updatedInvestment.getCryptoInvestedQuantity() + amount);
            updatedInvestment.setPurchasePrice(newPurchasePrice);
            updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() + amount);
        }

        else {
            crypto.setInvestorsAmount(crypto.getInvestorsAmount() + 1);
            Investment newInvestment = new Investment(crypto, crypto.getPrice(), amount);
            portfolio.getInvestments().add(newInvestment);
        }

        crypto.setAvailableAmount(crypto.getAvailableAmount() - amount);
        cryptoRepository.updateCrypto(crypto);
        portfolioRepository.updatePortfolio(portfolio);
        saveBuyTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));
    }

    // Vende uma criptomoeda do portfólio
    public void sellCrypto(String userID, String portfolioID, String cryptoName, double amount) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new PortfolioNotFoundException("Portfólio não encontrado");

        CryptoCurrency crypto = loadCryptoByName(cryptoName);

        if (!hasCrypto(cryptoName, portfolio))
            throw new IllegalArgumentException("Criptomoeda não encontrada no portfólio: " + cryptoName);

        if (amount <= 0)
            throw new IllegalArgumentException("Quantidade para venda deve ser maior que zero");

        if (portfolio.getAssetAmount(cryptoName) < amount)
            throw new IllegalArgumentException("Quantidade da criptomoeda no portfólio é insuficiente");

        portfolio.setBalance(portfolio.getBalance() + amount * crypto.getPrice());
        Investment updatedInvestment = findInvestment(portfolio, cryptoName);

        if (updatedInvestment.getCryptoInvestedQuantity() - amount == 0) {
            crypto.setInvestorsAmount(crypto.getInvestorsAmount() - 1);
            portfolio.getInvestments().remove(updatedInvestment);
        }

        else
            updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() - amount);

        crypto.setAvailableAmount(crypto.getAvailableAmount() + amount);
        cryptoRepository.updateCrypto(crypto);
        portfolioRepository.updatePortfolio(portfolio);
        saveSellTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));
    }
}