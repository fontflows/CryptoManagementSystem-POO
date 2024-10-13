package com.cryptomanager.services;

import com.cryptomanager.models.CryptoCurrency;
import com.cryptomanager.models.Investment;
import com.cryptomanager.repositories.CryptoRepository;

public class PortfolioService extends CryptoService{
    private final Investment[] investments;

    public PortfolioService(CryptoRepository cryptoRepository, Investment[] investments) {
        super(cryptoRepository);
        this.investments = investments;
    }

    public double calcularValorTotal() {
        double totalValue = 0.0;
        for (Investment investment : investments) {
            // Obtém a criptomoeda e seu preço atual
            CryptoCurrency cryptoCurrency = investment.getCryptoCurrency();
            double actualPrice = cryptoCurrency.getPrice(); // O preço é armazenado na própria classe CryptoCurrency
            int quantity = investment.getCryptoInvestedQuantity();

            // Adiciona o valor do investimento ao valor total
            totalValue += actualPrice*quantity;
        }
        return totalValue;
    }
}