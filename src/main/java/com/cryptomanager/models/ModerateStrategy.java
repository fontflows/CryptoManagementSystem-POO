package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia moderada.
 */
public class ModerateStrategy extends InvestmentStrategy{
    /** Construtor ModerateStrategy
     * Utiliza-se do construtor da superclasse para definir os campos de "Name" e "RiskQuota".
     */
    public ModerateStrategy() {
         super("MODERATE", 2);
    }
}