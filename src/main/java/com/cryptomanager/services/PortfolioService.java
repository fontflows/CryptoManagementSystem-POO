package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.InvestmentStrategy;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService{

    private final PortfolioRepository portfolioRepository;
    private final List<Portfolio> portfolioList;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, List<Portfolio> portfolioList) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioList = portfolioList;
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
        if (portfolioRepository.isValidPortfolio(portfolio)) {
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

    public CryptoCurrency suggestCryptoCurrency(Portfolio portfolio) {
        return portfolio.getInvestmentStrategy().getRandomCrypto();
    }

    public void cryptoConverterInPortfolio(String fromCryptoName, String toCryptoName, double balance, String userId, String IDPortfolio,
                                           InvestmentStrategy originalInvestmentStrategy, List<Investment> originalInvestmentList){

        Portfolio originalPortfolio = new Portfolio(IDPortfolio, userId, originalInvestmentList, originalInvestmentStrategy);
        PortfolioRepository portfolioRepository = new PortfolioRepository();
        portfolioRepository.editOriginalPortfolio(originalPortfolio, fromCryptoName, toCryptoName, balance);
    }

    public String findUserIdByCryptoName(String cryptoName){
        for (Portfolio portfolio : portfolioList) {
            for (Investment investment : portfolio.getInvestments()) {
                if (investment.getCryptoCurrency().getName().equalsIgnoreCase(cryptoName))
                    return portfolio.getUserId();
            }
        }
        return null;
    }

    public List<Investment> findInvestmentListByCryptoName(String cryptoName){
        for (Portfolio portfolio : portfolioList) {
            for (Investment investment : portfolio.getInvestments()) {
                if (investment.getCryptoCurrency().getName().equals(cryptoName))
                    return portfolio.getInvestments();
            }
        }
        return null;
    }

    public InvestmentStrategy findInvestmentStrategyByCryptoName(String cryptoName){
        for (Portfolio portfolio : portfolioList) {
            for (Investment investment : portfolio.getInvestments()) {
                if (investment.getCryptoCurrency().getName().equals(cryptoName))
                    return portfolio.getInvestmentStrategy();
            }
        }
        return null;
    }


    public void convertCrypto(String PortfolioId, String fromCryptoName, String toCryptoName, double balance) {
        if (fromCryptoName == null || toCryptoName == null)
            throw new IllegalArgumentException("Parâmetros não podem ser nulos.");

        String userId = findUserIdByCryptoName(fromCryptoName);
        List<Investment> originalInvestmentList = findInvestmentListByCryptoName(fromCryptoName);
        InvestmentStrategy originalInvestmentStrategy = findInvestmentStrategyByCryptoName(fromCryptoName);

        if (userId == null || PortfolioId == null || originalInvestmentList == null || originalInvestmentStrategy == null)
            throw new IllegalArgumentException("Portfolio associado não localizado. Conversao não realizada");

        cryptoConverterInPortfolio(fromCryptoName, toCryptoName, balance, userId, PortfolioId, originalInvestmentStrategy, originalInvestmentList);
    }
}