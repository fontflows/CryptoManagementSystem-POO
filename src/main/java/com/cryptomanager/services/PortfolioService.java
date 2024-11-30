package com.cryptomanager.services;

import com.cryptomanager.exceptions.PortfolioLoadException;
import com.cryptomanager.exceptions.PortfolioNotFoundException;
import com.cryptomanager.models.*;
import com.cryptomanager.repositories.CryptoRepository;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

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
        try {
            double totalValue = 0.0;
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);

            for (Investment investment : portfolio.getInvestments()) {
                CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
                double actualPrice = cryptoCurrency.getPrice();
                double quantity = investment.getCryptoInvestedQuantity();
                totalValue += actualPrice * quantity;
            }

            return totalValue;
        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado para o usuário " + userId + ": " + e.getMessage(), e);
        }
    }

    public static Investment findInvestment(Portfolio portfolio, String cryptoName) {
        for (Investment investment : portfolio.getInvestments()) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return investment;
        }
        throw new NoSuchElementException("Investimento não encontrado para a criptomoeda " + cryptoName);
    }

    public static boolean hasCrypto(String cryptoName, Portfolio portfolio) {
        for (Investment investment : portfolio.getInvestments())
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName.trim()))
                return true;
        return false;
    }

    public CryptoCurrency suggestCryptoCurrency(String userID, String portfolioID) {
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            try {
                InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();

                try {
                    InvestmentStrategyService.updateInvestmentStrategyList(investmentStrategy);
                    return getRandomCrypto(investmentStrategy);

                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Nenhuma criptomoeda disponível para sugestão na estratégia " + investmentStrategy.getInvestmentStrategyName());
                }

            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("Estratégia de investimento não configurada para o portfólio.");
            }

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado : " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor ao sugerir criptomoeda: " + e.getMessage(), e);
        }
    }

    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            if (strategyName.equals(portfolio.getInvestmentStrategy().getInvestmentStrategyName()))
                return; // Caso selecione a mesma estratégia, não é necessário alterar nada

            InvestmentStrategy strategy = getInvestmentStrategyByName(strategyName);

            portfolio.setInvestmentStrategy(strategy);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor durante a aplicação da estratégia: " + e.getMessage(), e);
        }
    }

    public void addBalance(String userID, String portfolioID, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
            portfolio.setBalance(portfolio.getBalance() + amount);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor ao adicionar saldo: " + e.getMessage(), e);
        }
    }

    public void redeemBalance(String userID, String portfolioID, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgatar deve ser maior que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);

            if (amount > portfolio.getBalance())
                throw new IllegalArgumentException("Valor inserido para resgatar é maior que o saldo disponível");

            portfolio.setBalance(portfolio.getBalance() - amount);
            portfolioRepository.updatePortfolio(portfolio);

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor ao resgatar saldo: " + e.getMessage(), e);
        }
    }

    public void buyCrypto(String userID, String portfolioID, String cryptoName, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Quantidade para compra deve ser maior que zero");

        try {
            Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
            CryptoCurrency crypto = loadCryptoByName(cryptoName);

            double totalCost = amount * crypto.getPrice();
            if (portfolio.getBalance() < totalCost)
                throw new IllegalArgumentException("Saldo disponível não é suficiente para essa compra");

            portfolio.setBalance(portfolio.getBalance() - totalCost);

            if (hasCrypto(cryptoName, portfolio)) {
                Investment updatedInvestment = findInvestment(portfolio, cryptoName);
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

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor ao comprar criptomoeda: " + e.getMessage(), e);
        }
    }

    public void sellCrypto(String userID, String portfolioID, String cryptoName, double amount) {
        try {
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
            }

            else
                updatedInvestment.setCryptoInvestedQuantity(updatedInvestment.getCryptoInvestedQuantity() - amount);

            crypto.setAvailableAmount(crypto.getAvailableAmount() + amount);
            cryptoRepository.updateCrypto(crypto);
            portfolioRepository.updatePortfolio(portfolio);
            saveSellTransaction(portfolio.getUserId(), portfolio.getId(), new Investment(crypto, crypto.getPrice(), amount));

        } catch (NoSuchElementException e) {
            throw new PortfolioNotFoundException("Portfólio não encontrado: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PortfolioNotFoundException("Erro interno do servidor ao vender criptomoeda: " + e.getMessage(), e);
        }
    }
}