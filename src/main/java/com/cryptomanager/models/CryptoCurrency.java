package com.cryptomanager.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classe modelo da estrutura padrão das criptomoedas a serem administradas no sistema Swagger.
 */
@Schema(description = "Modelo que representa uma criptomoeda")
public class CryptoCurrency {
    @Schema(description = "Nome da criptomoeda", example = "Bitcoin")
    private final String name;

    @Schema(description = "Preço atual da criptomoeda", example = "50000.0")
    private double price;

    @Schema(description = "Taxa de crescimento da criptomoeda", example = "0.05")
    private double growthRate;

    @Schema(description = "Capitalização de mercado da criptomoeda", example = "900000000000.0")
    private double marketCap;

    @Schema(description = "Volume negociado nas últimas 24 horas", example = "20000000.0")
    private double volume24h;

    @Schema(description = "Fator de risco da criptomoeda (1-3)", example = "3")
    private int riskFactor;

    @Schema(description = "Quantidade total de unidades da criptomoeda", example = "10000000")
    private final double totalAmount;

    @Schema(description = "Quantidade da criptomoeda disponível para compra", example = "20000000")
    private double availableAmount;

    @Schema(description = "Quantidade de clientes que possuem esta criptomoeda no portfólio", example = "50")
    private int investorsAmount = 0;

    /** Construtor padrao da classe CryptoCurrency
     * @param name Recebe o nome da criptomoeda.
     * @param price Recebe o preco da criptomoeda.
     * @param growthRate Recebe a taxa de crescimento da criptomoeda.
     * @param riskFactor Recebe a taxa do fator de risco da criptomoeda no mercado.
     * @param totalAmount Recebe a quantia total da criptomoeda existente.
     */
    public CryptoCurrency(String name, double price, double growthRate, int riskFactor, double totalAmount) {
        if (price <= 0)
            throw new IllegalArgumentException("O preço deve ser maior que zero.");

        if (growthRate <= -1)
            throw new IllegalArgumentException("A taxa de crescimento deve ser maior que -1.");

        if (riskFactor < 1 || riskFactor > 3)
            throw new IllegalArgumentException("O fator de risco deve estar entre 1 e 3.");

        if (totalAmount < 0)
            throw new IllegalArgumentException("Quantidade total de unidades da criptomoeda deve ser positiva.");

        this.name = name;
        this.price = price;
        this.growthRate = growthRate;
        this.riskFactor = riskFactor;
        this.totalAmount = totalAmount;
        this.availableAmount = totalAmount;
    }

    /** Metodo responsavel por obter o nome da criptomoeda.
     * @return Retorna o nome da criptomoeda.
     */
    public String getName() {
        return name;
    }

    /** Metodo responsavel por obter o preco da criptomoeda.
     * @return Retorna o preco da criptomoeda.
     */
    public double getPrice() {
        return price;
    }

    /** Metodo responsavel por atribuir certo preco a criptomoeda manipulada.
     * @param price Recebe o preco a ser relacionado a dada criptomoeda.
     */
    public void setPrice(double price) {
        if (price <= 0) { throw new IllegalArgumentException("O preço deve ser maior que zero."); }
        this.price = price;
    }

    /** Metodo responsavel por obter a capitalização de mercado da criptomoeda.
     * @return Retorna o valor da capitalização de mercado da criptomoeda.
     */
    public double getMarketCap() { return marketCap;}

    /** Metodo responsavel por atribuir a capitalização de mercado da criptomoeda.
     * @param marketCap Recebe o valor da capitalização de mercado da criptomoeda.
     */
    public void setMarketCap(double marketCap){
        if (marketCap < 0) { throw new IllegalArgumentException("O Market Cap deve ser positivo."); }
        this.marketCap = marketCap;
    }

    /** Metodo responsavel por obter a taxa de crescimento da criptomoeda.
     * @return Retorna a taxa de crescimento da criptomoeda.
     */
    public double getGrowthRate() { return growthRate;}

    /** Metodo responsavel por atribuir a taxa de crescimento da criptomoeda.
     * @param growthRate Recebe o valor da taxa de crescimento da criptomoeda.
     */
    public void setGrowthRate(double growthRate) {
        if (growthRate <= -1) { throw new IllegalArgumentException("A taxa de crescimento deve ser maior que -1."); }
        this.growthRate = growthRate;
    }

    /** Metodo responsavel por obter o total de criptomoedas negociadas em um dia.
     * @return Retorna o total de criptomoedas acumuladas em negociacoes, durante um dia.
     */
    public double getVolume24h() {return volume24h;}

    /** Metodo responsavel por atribuir o total de criptomoedas negociadas em um dia.
     * @param volume24h Recebe o total de criptomoedas acumuladas em negociacoes, durante um dia.
     */
    public void setVolume24h(double volume24h) {
        if (volume24h < 0) { throw new IllegalArgumentException("O volume em 24h não pode ser negativo."); }
        this.volume24h = volume24h;
    }

    /** Metodo responsavel por obter o fator de risco associado a criptomoeda.
     * @return Retorna o fator de risco relacionado a criptomoeda manipulada.
     */
    public int getRiskFactor() {return riskFactor;}

    /** Metodo responsavel por atribuir o fator de risco associado a criptomoeda.
     * @param riskFactor Recebe o fator de risco relacionado a criptomoeda manipulada.
     */
    public void setRiskFactor(int riskFactor) {
        if (riskFactor < 1 || riskFactor > 3) { throw new IllegalArgumentException("O fator de risco deve estar entre 1 e 3."); }
        this.riskFactor = riskFactor;
    }

    /** Metodo responsavel por atribuir a quantia total de criptomoedas disponiveis.
     * @param availableAmount Recebe o total de criptomoedas disponiveis no sistema.
     */
    public void setAvailableAmount(double availableAmount){
        if (availableAmount < 0) { throw new IllegalArgumentException("Quantidade de criptomoeda disponível deve ser positiva"); }
        this.availableAmount = availableAmount;
    }

    /** Metodo responsavel por obter o total de criptomoedas disponiveis.
     * @return Retorna o total de criptomoedas disponiveis no sistema.
     */
    public double getAvailableAmount() {
        return availableAmount;
    }

    /** Metodo responsavel por obter o total existente de certa criptomoeda
     * @return Retorna o total existente de uma criptomoeda.
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /** Metodo responsavel por obter o total de investidores que possuem investimentos em determinada criptomoeda.
     * @return Retorna o total de investidores que detem acoes em certa criptomoeda.
     */
    public int getInvestorsAmount() {
        return investorsAmount;
    }

    /** Metodo responsavel por atribuir o total de investidores de uma criptomoeda.
     * @param investorsAmount Recebe a quantia de investidores relacionadas a uma criptomoeda.
     */
    public void setInvestorsAmount(int investorsAmount) {
        this.investorsAmount = investorsAmount;
    }

    /** Metodo que sobrepoe a funcionalidade padrão toString()
     * @return Retorna a impressão das informações, de maneira formatada.
     */
    @Override
    public String toString() {
        return name + "," + price + "," + growthRate + "," + marketCap + "," + volume24h + "," + riskFactor + "," + investorsAmount + "," + totalAmount + "," + availableAmount;
    }
}