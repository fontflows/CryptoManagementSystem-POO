package com.cryptomanager.services;

import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    public double calculateTotalValue(String userId, String portfolioId) {
        double totalValue = 0.0;
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

        if (portfolio == null) {
            throw new IllegalArgumentException("Portfólio não encontrado.");
        }

        for (Investment investment : portfolio.getInvestments()) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice(); // O preço é armazenado na própria classe CryptoCurrency
            double quantity = investment.getCryptoInvestedQuantity();

            // Adiciona logs para depuração
            System.out.println("Nome da Criptomoeda: " + cryptoCurrency.getName());
            System.out.println("Preço Atual: " + actualPrice);
            System.out.println("Quantidade Investida: " + quantity);

            // Adiciona o valor do investimento ao valor total
            totalValue += actualPrice * quantity;
        }
        return totalValue;
    }

    //Adiciona um portfólio no arquivo
    public void addPortfolio(Portfolio portfolio) throws IOException {
        portfolioRepository.addPortfolio(portfolio);
    }

    //Retorna investimento pelo nome da crypto
    public static Investment findInvestment(Portfolio portfolio, String cryptoName) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equals(cryptoName)) {
                return investment;
            }
        }
        throw new IllegalArgumentException("Investimento não encontrado");
    }


    // Verifica se um portfólio contém um ativo específico
    public static boolean hasAsset(String assetName, Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equals(assetName)) {
                return true;
            }
        }
        return false;
    }

    public CryptoCurrency suggestCryptoCurrency(String userID, String portfolioID) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

        if (portfolio == null)
            throw new IllegalArgumentException("IDs invalidos");

        InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();

        if (investmentStrategy == null)
            throw new IllegalArgumentException("Estratégia de investimento não definida");

        return getRandomCrypto(investmentStrategy);
    }

    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        portfolio.setInvestmentStrategy(getInvestmentStrategyByName(strategyName));
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void addBalance(String userID, String portfolioID, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior que zero");
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        portfolio.setBalance(portfolio.getBalance() + amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void redeemBalance(String userID, String portfolioID, double amount) {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (amount > portfolio.getBalance())
            throw new IllegalArgumentException("Valor inserido para resgate é maior que o saldo disponível");
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgate deve ser maior que zero");
        portfolio.setBalance(portfolio.getBalance() - amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void buyCrypto(String userID, String portfolioID, String cryptoName, double amount) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (portfolio == null)
            throw new IllegalArgumentException("IDs invalidos");

        CryptoCurrency crypto = cryptoRepository.loadCryptoByName(cryptoName);
        if (crypto == null)
            throw new IllegalArgumentException("Nome da Criptomoeda não encontrado: " + cryptoName);

        if (portfolio.getBalance() < amount * crypto.getPrice())
            throw new IllegalArgumentException("Saldo disponível não é suficiente para essa compra");

        portfolio.setBalance(portfolio.getBalance() - amount * crypto.getPrice());

        if (hasAsset(cryptoName, portfolio)) {
            Investment updatedInvestment = findInvestment(portfolio, cryptoName);
            updatedInvestment.setPurchasePrice(crypto.getPrice());
            updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() + amount);
        }

        else {
            Investment newInvestment = new Investment(crypto, crypto.getPrice(), amount);
            portfolio.getInvestments().add(newInvestment);
        }
        portfolioRepository.updatePortfolio(portfolio);
    }

    public static int findInvestmentIndex(Portfolio portfolio, String cryptoName){
        for (int i = 0; i < portfolio.getInvestments().size(); i++)
            if (portfolio.getInvestments().get(i).getCryptoCurrency().getName().equalsIgnoreCase(cryptoName)){
                return i;
        }
        return -1;
    }
}