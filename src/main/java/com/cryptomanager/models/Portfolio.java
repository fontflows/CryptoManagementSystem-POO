package com.cryptomanager.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema(description = "Modelo que representa um portfólio de investimentos")
public class Portfolio {
    @Schema(description = "ID do portfólio", example = "PORTFOLIO-1")
    private String id; // ID do portfolio

    @Schema(description = "ID do usuário que possui o portfólio", example = "USER-1")
    private String userId; // ID do usuário

    @Schema(description = "Lista de investimentos no portfólio")
    private List<Investment> investments; // Lista de investimentos

    @Schema(description = "Estratégia de investimento do portfólio")
    private String investmentStrategy;

    public Portfolio(String id, String userId, List<Investment> investments, String investmentStrategy) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("portfolioId não pode ser nulo ou vazio.");

        if (userId == null || userId.isEmpty())
            throw new IllegalArgumentException("userId não pode ser nulo ou vazio.");

        this.id = id;
        this.userId = userId;
        this.investments = investments != null ? investments : new ArrayList<>(); // Inicializa com a lista recebida
        if(Objects.equals(investmentStrategy, "Aggressive") || Objects.equals(investmentStrategy, "Moderate") || Objects.equals(investmentStrategy, "Conservative"))
            this.investmentStrategy = investmentStrategy;
        else{
            this.investmentStrategy = "Conservative"; //estrategia padrao
        }
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

    public String getInvestmentStrategy() { return investmentStrategy; }

    public void setId(String id) { this.id = id; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setInvestments(List<Investment> investments) { this.investments = investments; }

    public void setInvestmentStrategy(String investmentStrategy) { this.investmentStrategy = investmentStrategy; }

    public Double getAssetAmount(String assetName) {
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equals(assetName))
                return investment.getCryptoInvestedQuantity(); // Retorna a quantidade
        }
        return null; // Retorna null se o ativo não for encontrado
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Portfolio other)) return false;

        return id.equals(other.id) && userId.equals(other.userId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(userId).append(",").append(investmentStrategy).append("\n");
        for (Investment investment : investments) {
            sb.append(investment.getCryptoCurrency().getName()).append(",")
                    .append(investment.getCryptoCurrency().getPrice()).append(",")
                    .append(investment.getCryptoCurrency().getGrowthRate()).append(",")
                    .append(investment.getCryptoCurrency().getMarketCap()).append(",")
                    .append(investment.getCryptoCurrency().getVolume24h()).append(",")
                    .append(investment.getCryptoCurrency().getRiskFactor()).append(",")
                    .append(investment.getCryptoInvestedQuantity()).append(",")
                    .append(investment.getPurchasePrice())
                    .append("\n");
        }
        return sb.toString();
    }
}
