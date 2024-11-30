package com.cryptomanager.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.services.InvestmentStrategyService.getInvestmentStrategyByName;

/**
 * Classe modelo da estrutura padrão do portfólio de investimentos dos usuários cadastrados no sistema Swagger.
 */

@Schema(description = "Modelo que representa um portfólio de investimentos")
public class Portfolio {

    @Schema(description = "ID do portfólio", example = "PORTFOLIO-1")
    private final String id; // ID do portfolio

    @Schema(description = "ID do usuário que possui o portfólio", example = "USER-1")
    private final String userId; // ID do usuário

    @Schema(description = "Lista de investimentos no portfólio")
    private List<Investment> investments = new ArrayList<>();// Lista de investimentos

    @Schema(description = "Estratégia de investimento do portfólio")
    private InvestmentStrategy investmentStrategy;

    @Schema(description = "Saldo disponível no portfólio")
    private double balance;

    /** Construtor padrão da classe Portfolio.
     * @param id Recebe o ID do portfólio.
     * @param userId Recebe o ID do usuário do portfólio.
     * @param investmentStrategy Recebe o tipo de estratégia de investimento do portfólio.
     * @param balance Recebe o ID do portfólio.
     * @throws IOException Exceção lançada, caso uma das entradas informadas esteja em um padrão inválido para o sistema.
     */
    public Portfolio(String id, String userId, String investmentStrategy, double balance) throws IOException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("PortfolioID não pode ser nulo ou vazio.");

        if (userId == null || userId.isEmpty())
            throw new IllegalArgumentException("UserID não pode ser nulo ou vazio.");

        if(balance < 0)
            throw new IllegalArgumentException("Saldo não pode ser negativo");

        this.id = id;
        this.userId = userId;
        this.investmentStrategy = getInvestmentStrategyByName(investmentStrategy);
        this.balance = balance;
    }

    /** Método responsável por obter o ID do portfólio.
     * @return Retorna o ID do portfólio.
     */
    public String getId() {
        return id;
    }

    /** Método responsável por obter o ID do usuário do portfólio.
     * @return Retorna o ID do usuário do portfólio.
     */
    public String getUserId() {
        return userId;
    }

    /** Método responsável por obter a lista de investimentos associada ao portfólio.
     * @return Retorna a lista de investimentos do portfólio.
     */
    public List<Investment> getInvestments() {
        return investments;
    }

    /** Método responsável por obter o saldo presente no portfólio.
     * @return Retorna o saldo do portfólio.
     */
    public double getBalance() {
        return balance;
    }

    /** Método que atribui certo valor ao portfólio associado.
     * @param balance Recebe o saldo para ser atribuído ao portfólio.
     */
    public void setBalance(double balance) {
        if (balance < 0)
            throw new IllegalArgumentException("Saldo não pode ser negativo");

        this.balance = balance;
    }

    /** Método que obtém a estratégia de investimento associada ao portfólio do usuário.
     * @return Retorna o tipo de estratégia de investimento do portfólio.
     */
    public InvestmentStrategy getInvestmentStrategy() {
        return investmentStrategy;
    }

    /** Método que atribuia estratégia de investimento associada ao portfólio do usuário.
     */
    public void setInvestmentStrategy(InvestmentStrategy investmentStrategy) {
        this.investmentStrategy = investmentStrategy;
    }

    /** Método que informa o valor relacionado à criptomoeda informada pelo usuário.
     * @param assetName Recebe o nome da criptomoeda declarada pelo usuário.
     * @return Retorna a quantia do saldo associado ao praâmetro "assetName".
     */
    public Double getAssetAmount(String assetName) {
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(assetName.trim()))
                return investment.getCryptoInvestedQuantity(); // Retorna a quantidade
        }

        return null; // Retorna null se o ativo não for encontrado
    }

    /** Método que realiza comparações entre diferentes objetos relacionados à classe Portfolio.
     * @param obj Recebe o objeto genérico recebido como parâmetro de comparação.
     * @return Retorna o valor booleano da comparação realizada pelo método.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Portfolio other)) return false;

        return id.equals(other.id) && userId.equals(other.userId);
    }

    /** Método que sobrecarrega a funcionalidade padrão toString()
     * @return Retorna a impressão das informações, de maneira formatada.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(userId).append(",").append(investmentStrategy.getInvestmentStrategyName()).append(",").append(balance).append("\n");

        for (Investment investment : investments) {
            sb.append(investment.getCryptoCurrency().getName()).append(",")
                    .append(investment.getCryptoInvestedQuantity()).append(",")
                    .append(investment.getPurchasePrice())
                    .append("\n");
        }

        return sb.toString();
    }
}