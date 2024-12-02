package com.cryptomanager.models;

/**
 * Classe-filha responsavel por suportar a classe-pai InvestmentStrategy, com o tipo de estrategia agressiva.
 */
public class AggressiveStrategy extends InvestmentStrategy{
    public AggressiveStrategy() {
        super("AGGRESSIVE", 3);
    }
}