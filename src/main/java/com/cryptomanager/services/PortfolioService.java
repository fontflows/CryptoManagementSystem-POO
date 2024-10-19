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

    public double calculateTotalValue(Portfolio portfolio) {
        double totalValue = 0.0;
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
        if (!portfolioRepository.isValidPortfolio(portfolio)) {
            System.err.println("Erro: Portfólio inválido.");
            return;
        }

        // Carregamento do portfólio existente
        Portfolio existingPortfolio = portfolioRepository.loadPortfolioByUserIdAndPortfolioId(portfolio.getUserId(), portfolio.getId());

        // Atualiza ou adiciona investimentos
        for (Investment investment : portfolio.getInvestments()) {
            if (existingPortfolio != null) {
                // Atualização da quantidade e do preço de compra, caso o ativo já exista
                if (portfolioRepository.hasAsset(investment.getCryptoCurrency().getName(), existingPortfolio)) {
                    existingPortfolio.getInvestments().remove(investment);
                }
                PortfolioRepository.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(), investment.getCryptoInvestedQuantity(), existingPortfolio);
            } else {
                PortfolioRepository.addAsset(investment.getCryptoCurrency(), investment.getPurchasePrice(), investment.getCryptoInvestedQuantity(), portfolio);
            }
        }
        // Salvamento de todos os portfólios de volta ao arquivo txt
        portfolioRepository.savePortfolio(portfolio); // Salva o portfólio atualizado
    }
}
