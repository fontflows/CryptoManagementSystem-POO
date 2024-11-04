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
public class PortfolioService{

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

    // Método para adicionar ou atualizar um portfólio
    public void addPortfolio(Portfolio portfolio) {
        System.out.println("Adicionando portfólio: " + portfolio); // Log para verificar o portfólio
        if (!portfolioRepository.isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return;
        }

        // Carregamento do portfólio existente
        Portfolio existingPortfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(portfolio.getUserId(), portfolio.getId());

        // Se o portfólio já existe, atualiza os investimentos
        if (existingPortfolio != null) {
            // Atualiza os investimentos existentes
            for (Investment newInvestment : portfolio.getInvestments()) {
                boolean investmentExists = false;

                // Verifica se o investimento já existe
                for (Investment existingInvestment : existingPortfolio.getInvestments()) {
                    if (existingInvestment.getCryptoCurrency().getName().equals(newInvestment.getCryptoCurrency().getName())) {
                        // Atualiza a quantidade investida e o preço de compra
                        existingInvestment.setPurchasePrice(((newInvestment.getPurchasePrice() * newInvestment.getCryptoInvestedQuantity()) +
                                (existingInvestment.getPurchasePrice() * existingInvestment.getCryptoInvestedQuantity())) /
                                (existingInvestment.getCryptoInvestedQuantity() + newInvestment.getCryptoInvestedQuantity())); // calcula preço médio
                        existingInvestment.setCryptoInvestedQuantity(existingInvestment.getCryptoInvestedQuantity() + newInvestment.getCryptoInvestedQuantity());
                        investmentExists = true;
                        break;
                    }
                }

                // Se o investimento não existe, adiciona um novo
                if (!investmentExists) {
                    existingPortfolio.getInvestments().add(newInvestment);
                }
            }
            // Salva o portfólio atualizado
            portfolioRepository.savePortfolio(existingPortfolio);
        } else {
            // Se o portfólio não existe, cria um novo
            portfolioRepository.savePortfolio(portfolio);
        }
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
        if (portfolio == null) { throw new IllegalArgumentException("IDs invalidos");}
        InvestmentStrategy investmentStrategy = portfolio.getInvestmentStrategy();
        return getRandomCrypto(investmentStrategy);
    }

    public void setPortfolioInvestmentStrategy(String userID, String portfolioID, String strategyName) throws IOException {
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        portfolio.setInvestmentStrategy(getInvestmentStrategyByName(strategyName));
        portfolioRepository.savePortfolio(portfolio);
    }

    public void addBalance(String userID, String portfolioID, double amount){
        if(amount <= 0)
            throw new IllegalArgumentException("Valor inserido para adicionar saldo deve ser maior que zero");
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        portfolio.setBalance(portfolio.getBalance() + amount);
        portfolioRepository.savePortfolio(portfolio);
    }

    public void redeemBalance(String userID, String portfolioID, double amount){
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userID, portfolioID);
        if(amount > portfolio.getBalance())
            throw new IllegalArgumentException("Valor inserido para resgate é maior que o saldo disponível");
        if(amount <= 0)
            throw new IllegalArgumentException("Valor inserido para resgate deve ser maior que zero");
        portfolio.setBalance(portfolio.getBalance() - amount);
        portfolioRepository.savePortfolio(portfolio);
    }
}
