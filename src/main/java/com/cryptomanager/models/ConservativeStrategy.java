package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia conservadora.
 */
public class ConservativeStrategy extends InvestmentStrategy {
    /** Construtor ConservativeStrategy
     * Utiliza-se do construtor da superclasse para definir os campos de "Name" e "RiskQuota".
     */
    public ConservativeStrategy() {
        super("CONSERVATIVE", 1);
    }
}