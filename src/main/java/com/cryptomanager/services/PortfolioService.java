package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;

public class PortfolioService{
    private final Investment[] investments;

    public PortfolioService(Investment[] investments) {
        this.investments = investments;
    }

    public double calculateTotalValue() {
        double totalValue = 0.0;
        for (Investment investment : investments) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice(); // O preço é armazenado na própria classe CryptoCurrency
            double quantity = investment.getCryptoInvestedQuantity();

            // Adiciona o valor do investimento ao valor total
            totalValue += actualPrice*quantity;
        }
        return totalValue;
    }
}