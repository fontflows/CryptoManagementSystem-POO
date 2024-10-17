package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private final String id; // ID do portfolio
    private final String userId; // ID do usuário
    private final List<Investment> investments; // Lista de investimentos

    public Portfolio(String id, String userId) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("portfolioId não pode ser nulo ou vazio.");

        if (userId == null || userId.isEmpty())
            throw new IllegalArgumentException("userId não pode ser nulo ou vazio.");

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

    public Double getAssetAmount(String assetName) {
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equals(assetName))
                return investment.getCryptoInvestedQuantity(); // Retorna a quantidade
        }
        return null; // Retorna null se o ativo não for encontrado
    }

    public boolean hasAsset(String assetName) { // Verifica se há algum ativo existente
        for (Investment investment : investments)
            if (investment.getCryptoCurrency().getName().equals(assetName))
                return true;
        return false;
    }

    public void addAsset(CryptoCurrency cryptoCurrency, double purchasePrice, double cryptoInvestedQuantity) { // Adiciona ativo
        Investment existingInvestment = null;

        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equals(cryptoCurrency.getName())) {
                existingInvestment = investment;
                break;
            }
        }

        if (existingInvestment != null) {
            // Atualiza o investimento existente, além de ter lógica de preço médio.
            double totalQuantity = existingInvestment.getCryptoInvestedQuantity() + cryptoInvestedQuantity;
            double totalValue = (existingInvestment.getPurchasePrice() * existingInvestment.getCryptoInvestedQuantity()) +
                    (purchasePrice * cryptoInvestedQuantity);
            double averagePrice = totalValue / totalQuantity;

            // Atualiza o investimento
            existingInvestment.setCryptoInvestedQuantity(totalQuantity);
            existingInvestment.setPurchasePrice(averagePrice);
        } else
            // Adiciona um novo investimento
            investments.add(new Investment(cryptoCurrency, purchasePrice, cryptoInvestedQuantity));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; /* Este trecho verifica se o objeto atual (this) é o mesmo que o objeto passado
        como argumento (obj). Se forem o mesmo objeto na memória, retorna true, indicando que são iguais.*/

        if (!(obj instanceof Portfolio other)) return false; /* O método verifica se obj é uma instância da classe
        Portfolio. Se não for, retorna false.
        A parte instanceof Portfolio other também realiza uma conversão segura de obj para Portfolio, atribuindo-o à
        variável other. Isso permite que acessar os atributos do objeto other mais adiante sem precisar fazer uma
        conversão adicional.*/

        return id.equals(other.id) && userId.equals(other.userId); /* Se obj é uma instância de Portfolio, o
        método compara os atributos id e userId do objeto atual (this) com os do objeto other.
        Se ambos os atributos forem iguais, o método retorna true. Caso contrário, retorna false. */
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