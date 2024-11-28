package com.cryptomanager.services;

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

    public double calculateTotalValue(String userId, String portfolioId) {
        double totalValue = 0.0;
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
        for (Investment investment : portfolio.getInvestments()) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice();
            double quantity = investment.getCryptoInvestedQuantity();
            // Adiciona o valor do investimento ao valor total
            totalValue += actualPrice * quantity;
        }
        
        return totalValue;
    }

    //Retorna investimento pelo nome da crypto
    public static Investment findInvestment(Portfolio portfolio, String cryptoName) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim())) {
                return investment;
            }
        }
        throw new IllegalArgumentException("Investimento não encontrado");
    }


    // Verifica se um portfólio contém um ativo específico
    public static boolean hasCrypto(String cryptoName, Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim())) {
                return true;
            }
        }
        return false;
    }

    public CryptoCurrency suggestCryptoCurrency(String userID, String portfolioID) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();
        InvestmentStrategyService.updateInvestmentStrategyList(investmentStrategy);

        if(investmentStrategy.getSuggestedCryptos().isEmpty()){
            throw new IllegalArgumentException("Nennhuma criptomoeda " + investmentStrategy.getInvestmentStrategyName() + " disponível para sugestão");
        }

        return getRandomCrypto(investmentStrategy);
    }

    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if(strategyName.equals(portfolio.getInvestmentStrategy().getInvestmentStrategyName())) return; //Caso selecione a mesma estratégia, não é necessario alterar nada

        portfolio.setInvestmentStrategy(getInvestmentStrategyByName(strategyName));
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void addBalance(String userID, String portfolioID, double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior que zero");
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        portfolio.setBalance(portfolio.getBalance() + amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void redeemBalance(String userID, String portfolioID, double amount) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if (amount > portfolio.getBalance())
            throw new IllegalArgumentException("Valor inserido para resgate é maior que o saldo disponível");
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgate deve ser maior que zero");
        portfolio.setBalance(portfolio.getBalance() - amount);
        portfolioRepository.updatePortfolio(portfolio);
    }

    public void buyCrypto(String userID, String portfolioID, String cryptoName, double amount) throws IOException {
        if (amount <= 0)
            throw new IllegalArgumentException("Quantidade para compra deve ser maior que zero");
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        CryptoCurrency crypto = loadCryptoByName(cryptoName);

        if (portfolio.getBalance() < amount * crypto.getPrice())
            throw new IllegalArgumentException("Saldo disponível não é suficiente para essa compra");

        portfolio.setBalance(portfolio.getBalance() - amount * crypto.getPrice());

        if (hasCrypto(cryptoName, portfolio)) {
            Investment updatedInvestment = findInvestment(portfolio, cryptoName);
            updatedInvestment.setPurchasePrice(
                    (crypto.getPrice()*amount + updatedInvestment.getCryptoInvestedQuantity()*updatedInvestment.getPurchasePrice())/(updatedInvestment.getCryptoInvestedQuantity() + amount)); //Calcula o preço médio
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
  
    public void sellCrypto(String userID, String portfolioID, String cryptoName, double amount) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
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
        } else {
            updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() - amount);
        }
        crypto.setAvailableAmount(crypto.getAvailableAmount() + amount);
        cryptoRepository.updateCrypto(crypto);
        portfolioRepository.updatePortfolio(portfolio);
        saveSellTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));
    }
}