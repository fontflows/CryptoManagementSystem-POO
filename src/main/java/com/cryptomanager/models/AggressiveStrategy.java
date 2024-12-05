package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia agressiva.
 */
public class AggressiveStrategy extends InvestmentStrategy{
    /** Construtor AggressiveStrategy
     * Utiliza-se do construtor da superclasse para definir os campos de "Name" e "RiskQuota".
     */
    public AggressiveStrategy() {
        super("AGGRESSIVE", 3);
    }
}