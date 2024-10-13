package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private final String id; // ID do portfolio
    private final String userId;
    private final List<Investment> investments;

    public Portfolio(String id, String userId) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("portfolioId não pode ser nulo ou vazio.");
        }

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId não pode ser nulo ou vazio.");
        }

        this.id = id;
        this.userId = userId;
        this.investments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<Investment> getInvestments() {
        return investments;
    }

    // Método para obter a quantidade de um ativo específico
    public Double getAssetAmount(String assetName) {
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equals(assetName)) {
                return (double) investment.getCryptoInvestedQuantity(); // Retorna a quantidade
            }
        }
        return null; // Retorna null se o ativo não for encontrado
    }

    // Método para validar se um ativo existe no portfólio
    public boolean hasAsset(String assetName) { // Argumento expressa o nome do ativo
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equals(assetName)) {
                return true; // Ativo encontrado
            }
        }
        return false; // Ativo não encontrado
    }

    public void addAsset(CryptoCurrency cryptoCurrency, double purchasePrice, int cryptoInvestedQuantity) {
        this.investments.add(new Investment(cryptoCurrency, purchasePrice, cryptoInvestedQuantity));
    }

    // Método equals para comparação
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Portfolio other)) return false;
        return id.equals(other.id) && userId.equals(other.userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Investment investment : investments) {
            sb.append(investment.getCryptoCurrency().getName())
                    .append(", Quantidade: ")
                    .append(getAssetAmount(investment.getCryptoCurrency().getName()))
                    .append(", Preço: ")
                    .append(investment.getPurchasePrice())
                    .append("\n"); // Adiciona nova linha entre os investimentos
        }
        return sb.toString(); // Retorna a string dos investimentos
    }
}
