package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia conservadora.
 */
public class ConservativeStrategy extends InvestmentStrategy {
    public ConservativeStrategy() {
        super("CONSERVATIVE", 1);
    }
}