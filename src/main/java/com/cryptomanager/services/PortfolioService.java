package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.models.Portfolio;
import com.cryptomanager.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService{

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public double calculateTotalValue(String userId, String portfolioId) {
        double totalValue = 0.0;
        Portfolio portfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(userId, portfolioId);
        for (Investment investment : portfolio.getInvestments()) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice(); // O preço é armazenado na própria classe CryptoCurrency
            double quantity = investment.getCryptoInvestedQuantity();

            // Adiciona o valor do investimento ao valor total
            totalValue += actualPrice*quantity;
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
            for (Investment newInvestment : portfolio.getInvestments()) {
                boolean investmentExists = false;

                // Verifica se o investimento já existe
                for (Investment existingInvestment : existingPortfolio.getInvestments()) {
                    if (existingInvestment.getCryptoCurrency().getName().equals(newInvestment.getCryptoCurrency().getName())) {
                        // Atualiza a quantidade investida e o preço de compra
                        existingInvestment.setPurchasePrice(((newInvestment.getPurchasePrice()*newInvestment.getCryptoInvestedQuantity()) + (existingInvestment.getPurchasePrice()*existingInvestment.getCryptoInvestedQuantity()))/existingInvestment.getCryptoInvestedQuantity() + newInvestment.getCryptoInvestedQuantity()); //calcula preço médio
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
}
