package com.cryptomanager.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cryptomanager.services.InvestmentStrategyService.getInvestmentStrategyByName;

/**
 * Classe modelo da estrutura padrao do portfolio de investimentos dos usuarios cadastrados no sistema Swagger.
 */

@Schema(description = "Modelo que representa um portfolio de investimentos")
public class Portfolio {

    @Schema(description = "ID do portfolio", example = "PORTFOLIO-1")
    private final String id; // ID do portfolio

    @Schema(description = "ID do usuario que possui o portfolio", example = "USER-1")
    private final String userId; // ID do usuario

    @Schema(description = "Lista de investimentos no portfolio")
    private List<Investment> investments = new ArrayList<>();// Lista de investimentos

    @Schema(description = "Estrategia de investimento do portfolio")
    private InvestmentStrategy investmentStrategy;

    @Schema(description = "Saldo disponivel no portfolio")
    private double balance;

    /** Construtor padrao da classe Portfolio.
     * @param id Recebe o ID do portfolio.
     * @param userId Recebe o ID do usuario do portfolio.
     * @param investmentStrategy Recebe o tipo de estrategia de investimento do portfolio.
     * @param balance Recebe o saldo do portfolio.
     * @throws IOException Excecao lancada, caso uma das entradas informadas esteja em um padrao invalido para o sistema.
     */
    public Portfolio(String id, String userId, String investmentStrategy, double balance) throws IOException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("PortfolioID nao pode ser nulo ou vazio.");

        if (userId == null || userId.isEmpty())
            throw new IllegalArgumentException("UserID nao pode ser nulo ou vazio.");

        if(balance < 0)
            throw new IllegalArgumentException("Saldo nao pode ser negativo");

        this.id = id;
        this.userId = userId;
        this.investmentStrategy = getInvestmentStrategyByName(investmentStrategy);
        this.balance = balance;
    }

    /** Metodo responsavel por obter o ID do portfolio.
     * @return Retorna o ID do portfolio.
     */
    public String getId() {
        return id;
    }

    /** Metodo responsavel por obter o ID do usuario do portfolio.
     * @return Retorna o ID do usuario do portfolio.
     */
    public String getUserId() {
        return userId;
    }

    /** Metodo responsavel por obter a lista de investimentos associada ao portfolio.
     * @return Retorna a lista de investimentos do portfolio.
     */
    public List<Investment> getInvestments() {
        return investments;
    }

    /** Metodo responsavel por obter o saldo presente no portfolio.
     * @return Retorna o saldo do portfolio.
     */
    public double getBalance() {
        return balance;
    }

    /** Metodo que atribui certo valor ao portfolio associado.
     * @param balance Recebe o saldo para ser atribuido ao portfolio.
     */
    public void setBalance(double balance) {
        if (balance < 0)
            throw new IllegalArgumentException("Saldo nao pode ser negativo");

        this.balance = balance;
    }

    /** Metodo que obtem a estrategia de investimento associada ao portfolio do usuario.
     * @return Retorna o tipo de estrategia de investimento do portfolio.
     */
    public InvestmentStrategy getInvestmentStrategy() {
        return investmentStrategy;
    }

    /** Metodo que atribuia estrategia de investimento associada ao portfolio do usuario.
     * @param investmentStrategy Recebe a estrategia de investimento que sera atribuida.
     */
    public void setInvestmentStrategy(InvestmentStrategy investmentStrategy) {
        this.investmentStrategy = investmentStrategy;
    }

    /** Metodo que informa o valor relacionado a criptomoeda informada pelo usuario.
     * @param assetName Recebe o nome da criptomoeda declarada pelo usuario.
     * @return Retorna a quantia do saldo associado ao parametro "assetName".
     */
    public Double getAssetAmount(String assetName) {
        for (Investment investment : investments) {
            if (investment.getCryptoCurrency().getName().equalsIgnoreCase(assetName.trim()))
                return investment.getCryptoInvestedQuantity(); // Retorna a quantidade
        }

        return null; // Retorna null se o ativo nao for encontrado
    }

    /** Metodo que realiza comparacoes entre diferentes objetos relacionados a classe Portfolio.
     * @param obj Recebe o objeto generico recebido como parametro de comparacao.
     * @return Retorna o valor booleano da comparacao realizada pelo metodo.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Portfolio other)) return false;

        return id.equals(other.id) && userId.equals(other.userId);
    }

    /** Metodo que sobrecarrega a funcionalidade padrao toString()
     * @return Retorna a impressao das informacoes, de maneira formatada.
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