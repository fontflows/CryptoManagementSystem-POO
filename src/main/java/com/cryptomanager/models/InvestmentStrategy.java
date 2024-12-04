package com.cryptomanager.models;

import java.util.ArrayList;
import java.util.List;

/** Classe modelo da estrutura padrao do nome da estrategia de investimentos de cada usuario do sistema.
 */
public class InvestmentStrategy {
    private final String name;
    private final List<CryptoCurrency> suggestedCryptos = new ArrayList<>();
    private final int riskQuota;

    /** Construtor padrao da classe InvestmentStrategy.
     * @param name Recebe o nome do investimento.
     * @param riskQuota Recebe a cota de risco da estrategia de investimento.
     */
    public InvestmentStrategy(String name, int riskQuota){
        this.name = name;
        this.riskQuota = riskQuota;
    }

    /** Metodo responsavel por obter o nome da estrategia de investimento do usuario.
     * @return Retorna o nome da estrategia de investimento.
     */
    public String getInvestmentStrategyName(){
        return name;
    }

    /** Metodo responsavel por obter a cota de risco da estrategia de investimento.
     * @return Retorna a cota de risco da estrategia de investimento.
     */
    public int getRiskQuota(){
            return riskQuota;
    }

    /** Metodo responsavel por obter as criptomoedas sugeridas para a estrategia de investimento informada pelo usuario do sistema.
     * @return Retorna a lista de criptomoedas adequadas/apropriadas para a estrategia de investimento declarada.
     */
    public List<CryptoCurrency> getSuggestedCryptos(){
        return suggestedCryptos;
    }
}